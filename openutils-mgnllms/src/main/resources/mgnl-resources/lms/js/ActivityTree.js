var API_1484_11;

/*
 * Classe che rappresenta un'activity tree,, contiene le informazioni del
 * tracking model sugli Objectives, le attività sospesa e quella corrente,
 * include le funzioni di sequencing, salva e recupera i dati del tracking model
 * dal server
 */ 
var ActivityTree = new Class({

	initialize: function(organization, scormPlayer,index, json, seqCollection) {
		this.index = index;
		this.current = null;
		this.suspendedActivity = null;
		this.organization = organization;
		this.scormPlayer = scormPlayer;
		this.context = scormPlayer.context;
		this.layout = scormPlayer.layout;
		this.seqCollection = seqCollection;
		this.root=null;
		this.objectives = new Hash();
		this.globalObjectives = new Array();
		this.name = organization.title;
		this.makeTree();
		
		this.globalObjectives = new Hash(this.globalObjectives.map(function(id){
				return new GlobalSharedObjective(id);
			}).associate(this.globalObjectives));
		
		this.preorder = new Array();
		this.preorderFull = new Array();
		this.preorderTreeTraversal(this.root);
		this.debounce = false;
		this.title = organization.title;
		this.json = json;
		
		if (this.json){
			this.fromJSON(this.json);
		}
	},
	
	/*
	 * Richiamata da scormPlayer sceglie che richiesta fare all'Overall
	 * Sequencing Process a seconda della presenza di un'attività sospesa
	 */ 
	start: function(){
		var req = this.suspendedActivity ? "resumeall" : !this.current ? "start" :"";
		this.overallSequencingProcess(req);
	},
	
	/*
	 * Funzione che imposta a false la proprietà debounce
	 */
	clearDebounce: function(){
		this.debounce = false;
	},
	
	/*
	 * Converte i Tracking Model e gli Objectives, ripuliti dalle proprietà da
	 * non persistere e dai riferimentio incrociati, in json
	 */
	toJSON: function(){
		var toEncode = new Hash({
			objectives: new Hash(),
			tracks: new Hash(),
			suspendedActivity: this.suspendedActivity ? this.suspendedActivity.getName() : "",
		});
		
		function recExplo(node,i){
			toEncode.tracks.set(node.getName(), node.options.data.track.toJSON().set("title",node.options.data.item.title).set("level",i));
			if (node.nodes){
				node.nodes.each(function(n){
					recExplo(n,i+1);
				})
			}
		}
		
		recExplo(this.root,0);
		
		this.objectives.each(function(obj,key){
			toEncode.objectives.set(key,obj.toJSON().set("organizationObjective",obj.node==this.root));
		},this)
		
		this.globalObjectives.each(function(obj,key){
			toEncode.objectives.set(key,obj.toJSON().set("globalObj",true));
		})
		
		return JSON.encode(toEncode);
	}, 
	
	/*
	 * Aggiorna il tracking model e gli obiettivi con i dati recuperati dal
	 * server in formato JSON, nel caso il campo json suspendedActivity sia
	 * diverso da null, trasforma l'identificatore dell'attività in un'attività
	 * (node) ricercandola all'interno dell'activity tree
	 */
	fromJSON: function(json){
		var h = new Hash(JSON.decode(json));
		new Hash(h.objectives).each(function(value,key){
			if (value.globalObj){
				new Hash(value).each(function(v,k){
					this.globalObjectives[key][k]=v;	
				},this)
			}else{
				new Hash(value).each(function(v,k){
					this.objectives[key][k]=v;	
				},this)
			}
		},this)
		
		function recSave(node){
			node.options.data.track.fromJSON(new Hash(h.tracks[node.getName()]));
			if (node.nodes){
				node.nodes.each(function(n){
					recSave(n);
				})
			}
		}
		
		recSave(this.root);
		if(h.current){
			this.current = this.root.findChild([h.current]);
		}
		if(h.suspendedActivity){
			this.suspendedActivity = this.root.findChild([h.suspendedActivity]);
		}
	},
	
	/*
	 * funzione ricorsiva che crea un array con tutte le attività foglie in
	 * ordine preorder e tutte le attività
	 */
	preorderTreeTraversal: function(node) {
		this.preorderFull.push(node);
		if(!node.nodes){
			this.preorder.push(node);
		}else{
			node.nodes.each(function(n){
				this.preorderTreeTraversal(n);
			},this);
		}
	},
	
	/*
	 * Queste funzioni sequono tutte la convenzione del nome:
	 * richiesta+"SequencingRequestProcess". Le funzioni sono implementazioni
	 * dello pseudocodice presente nel manuale SN dello SCORM. I commenti
	 * numerati all'interno del codice indicano quale "linea" dello pseudocodice
	 * implementa il codice adiacente
	 */
	
	/* Continue Sequencing Request Process [SB.2.7] */
	continueSequencingRequestProcess: function(target,dontTerminate) {
		
		/*
		 * The Continue Sequencing Request Subprocess assumes the current
		 * sequencing session has already begun. If it has begun and Sequencing
		 * Control Mode Flow is True for the current activity, the Flow
		 * Subprocess (refer to Section 4.8.5) is invoked from the current
		 * activity in a forward direction. If the Flow Subprocess identifies an
		 * activity, that activity is identified for delivery.
		 */
		
		if (!this.current) /* 1 */
			return {activity: null, exception: "SB.2.7-1"};
		if (this.current!=this.root) /* 2 */
			if (!this.current.owner.getControlMode().flow) /* 2.1 */
				return {activity: null, exception: "SB.2.7-2"};
		var fsp = this.flowSubprocess(this.current, "forward", false, dontTerminate); /* 3 */
		if (fsp.deliverable == false){
// if (fsp.activity == false){
			return {activity: null, endSs: fsp.endSs, exception: fsp.exception};
		}else {
			return {activity: fsp.activity, exception: null};
		}
	},
	
	/* Previous Sequencing Request Process [SB.2.8] */
	previousSequencingRequestProcess: function(target,dontTerminate) {
		if (!this.current) /* 1 */
			return {activity: null, exception: "SB.2.8-1"};
		if (this.current!=this.root) /* 2 */
			if (this.current.owner.getControlMode().flow == false) /* 2.1 */
				return {activity: null, exception: "SB.2.8-2"};
		var fsp = this.flowSubprocess(this.current, "backward", false); /* 3 */
		if (fsp.deliverable==false){
			return {activity: null, exception: fsp.exception};
		}else {
			return {activity: fsp.activity, exception: null};
		}
	},
	
	/* Start Sequencing Request Process [SB.2.5] */
	startSequencingRequestProcess: function() {
		/* 1 */
		if (this.current){
			return {activity: null, exception: "SB.2.5-1"};
		}
		/* 2 */ 
		if (!this.root.nodes){
			return {activity: this.root, exception: null};
		/* 3 */
		}else{
			var fs = this.flowSubprocess(this.root,'forward',true);
			if (fs.activity==null){
				return{
					activity: null,
					endSs: fs.endSs,
					exception: fs.exception,
				}
			}else{
				return{
					activity: fs.activity,
					exception: null,
				}
			}
		}
	},
	
	/* Choice Sequencing Request Process [SB.2.9] */
	choiceSequencingRequestProcess: function(target,dontTerminate){
		var temp = {activity: null, exception: null}
		var traverse; 
		
		if (!target){
			temp.exception = 'SB.2.9-1';
			return temp;
		}
		/* 3 */
		target.pathFromRoot().some(function(node){
			if (node != this.root){
				if (!node.owner.availableChildren().contains(node)){
					temp.exception = 'SB.2.9-2';
					return temp;
				}
			}
			/* 3.2 */
			var srcp = new RollupProcess(node,this).sequencingRulesCheckProcess(node,['hiddenfromchoice']);
			if (srcp!= null){
				temp.exception = 'SB.2.9-3';
				return temp;
			}
		},this);
		/* 4 */
		if (target != this.root){
			if(target.owner.getControlMode().choice == false){
				temp.exception = 'SB.2.9-4';
				return temp;
			}
		}
		/* 5 */
		var ancestor;
		if (this.current){
			var nodePath = target.pathFromRoot().reverse();
			var curPath = this.current.pathFromRoot().reverse();
			ancestor = curPath.pop();
			nodePath.pop();
			
			while(curPath.length && nodePath.length 
					&& curPath.getLast()==nodePath.getLast()){
				ancestor = curPath.pop();
				nodePath.pop();
			}
		}else{
			ancestor = this.root;
		}
		/* 7 */
		if (this.current == target){

		}else 
		/* 8 */
		if(this.current && this.current.owner == target.owner){
			var s = this.current.owner.nodes.indexOf(this.current);
			var e = this.current.owner.nodes.indexOf(target);
			var actPath = new Array();
			if (e>s) {
				actPath = this.current.owner.nodes.slice(s,e+1);
			}else{
				actPath = this.current.owner.nodes.slice(e+1,s+1).reverse();
			}
			if (actPath.length == 0){
				temp.exception = 'SB.2.9-5';
				return temp;
			}
			if (this.preorderFull.indexOf(target)>this.preorderFull.indexOf(this.current)){
				traverse = 'forward';
			}else{
				traverse = 'backward';
			}
			var i=0;
			do{
				var cats = this.choiceActivityTraversalSubprocess(actPath[i],traverse);
				i++;
				if (cats.deliverable == false){
					return {activity: null, exception: cats.exception};
				}
			}while(i<actPath.length);
		}else
		/* 9 */
		if (this.current == ancestor || this.current==null){
			var actPath = new Array();
			for(var n = target; n.owner && n!=ancestor; n=n.owner){
				actPath.push(n);
			}
			actPath=actPath.reverse();
			if (actPath.length == 0){
				temp.exception = 'SB.2.9-5';
				return temp;
			}
			var i = 0;
			do{
				var cats = this.choiceActivityTraversalSubprocess(actPath[i],'forward');
				if (cats.deliverable == false){
					temp.exception = cats.exception;
					return temp;
				}
				if (actPath[i].options.data.track.activityIsActive == false 
						&& (actPath[i]!=ancestor 
								&& actPath[i].options.data.item.sequencing.constrainedChoiceConsiderations.preventActivation== true)){
					temp.exception = 'SB.2.9-6';
					return temp;
				}
				i++;
			}while(i<actPath.length);
		}else
		/* 10 */
		if(target == ancestor){
			var actPath = new Array();
			for(var n = this.current.owner; n!=target.owner; n=n.owner){
				actPath.push(n);
			}
			if (actPath.length == 0){
				temp.exception = 'SB.2.9-7';
				return temp;
			}
			var i =0;
			do{
				if (actPath[i] != actPath.getLast()){
					if (actPath[i].getControlMode.choiceExit == false){
						temp.exception = 'SB.2.9-7';
						return temp;
					}
				}
				i++
			}while(i<actPath.length);
			
		}else
		if(ancestor.findChild([target.getName()])){
			/* 11 */
			
			var actPath = new Array();
// for(var n = this.current.owner; n!=ancestor; n=n.owner){
			for(var n = this.current; n!=ancestor; n=n.owner){
				actPath.push(n);
			}
// if (actPath.length == 0){
// temp.exception = 'SB.2.9-5';
// return temp;
// }
			var constrained = null;
			var i=0;
			do{
				/* 11.4.1 */
				if (actPath[i].getControlMode().choiceExit== false){
					temp.exception = 'SB.2.9-7';
					return temp;
				}
				/* 11.4.2 */
				if (constrained == null){
					if (actPath[i].options.data.item.sequencing.constrainedChoiceConsiderations.constrainChoice==true){
						constrained = actPath[i];
					}
				}
				i++;
			}while(i<actPath.length);
			/* 11.5 */
			var traverse;
			if (constrained != null){
				if (this.preorderFull.indexOf(target)>this.preorderFull.indexOf(constrained)){
					traverse = 'forward';
				}else{
					traverse = 'backward';
				}
				var cfs = this.choiceFlowSubprocess(constrained,traverse);
				var consider = cfs.activity;
				if(consider.findChild([target.getName()])==null
						&& target != consider
						&& target != constrained){
					temp.exception = 'SB.2.9-8';
					return temp;
				}
			}
			/* 11.6 */
			var actPath = new Array();
			for(var n = target.owner; n!= ancestor.owner; n = n.owner){
				actPath.push(n);
			}
			actPath = actPath.reverse();
			
			if (actPath.length == 0){
				temp.exception = 'SB.2.9-5';
				return temp;
			}
			if (this.preorderFull.indexOf(target)>this.preorderFull.indexOf(this.current)){
				/* 11.8.1 */
				var i=0;
				do{
					var cats = this.choiceActivityTraversalSubprocess(actPath[i],'forward');
					if (cats.deliverable == false){
						return {activity: null, exception: cats.exception};
					}
					if (actPath[i].options.data.track.activityIsActive == false 
							&& actPath[i] != ancestor
							&& actPath[i].options.data.item.sequencing.constrainedChoiceConsiderations.preventActivation == true){
						temp.exception = 'SB.2.9-6';
						return temp;
					}
					i++;
				}while(i<actPath.length);
			}else{
				actPath.push(target);
				if(actPath.some(function(node){
					return (node.options.data.track.activityIsActive == false
							&& node != ancestor
							&& node.options.data.item.sequencing.constrainedChoiceConsiderations.preventActivation == true);
				})){
					temp.exception = 'SB.2.9-6';
					return temp;	
				}
			}
		}
		if (!target.nodes){
			temp.activity = target;
			return temp;
		}
		var fs = this.flowSubprocess(target, 'forward', true)
		if (fs.deliverable == false){
			if(!dontTerminate){
				this.terminateDescendentAttemptsProcess(ancestor);
			}
			this.current = target;
			return {activity: null,  exception: 'SB.2.9-9'};
		}else{
			return {activity: fs.activity,  exception: null};
		}
	},
	
	/* Resume All Sequencing Request Process [SB.2.6] */
	resumeallSequencingRequestProcess: function(){
		if (this.current){
			return {activity: null, exception: "SB.2.6-1"};
		}
		if (!this.suspendedActivity){
			return {activity: null, exception: "SB.2.6-2"};
		}
		return {activity: this.suspendedActivity, exception: null};
	},
	
	/* Retry Sequencing Request Process [SB.2.10] */
	retrySequencingRequestProcess: function(){
		if (!this.current){
			return {activity: null, exception: "SB.2.10-1"};
		}
		if (this.current.options.data.track.activityIsActive == true 
				|| this.current.options.data.track.getActivityIsSuspended() == true){
			return {activity: null, exception: "SB.2.10-2"};
		}
		if (this.current.nodes){
			var fs = this.flowSubprocess(this.current, 'forward', true);
			if (fs.deliverable == false){
				return {activity: null, exception: "SB.2.10-3"};
			}else{
				return {activity: fs.activity, exception: null};
			}
		}else{
			return {activity: this.current, exception: null};
		}
	},
	
	/* Exit Sequencing Request Process [SB.2.11] */
	exitSequencingRequestProcess: function(){
		if (!this.current){
			return {endSs: false, exception: "SB.2.11-1"};
		}
		if (this.current.options.data.track.activityIsActive == true){
			return {endSs: false, exception: "SB.2.11-2"};
		}
		if (this.current == this.root){
			return {endSs: true, exception: null}
		}
		return {endSs: false, exception: null};
	},
	
	/* Jump Sequencing Request Process [SB.2.13] */
	jumpSequencingRequestProcess: function(target){
		/*
		 * Pseudocodice: If the Current Activity is Defined Then Exit Jump
		 * Sequencing Request Process (Delivery Request: n/a; Exception:
		 * SB.2.13-1)
		 * 
		 * 4.8.6.8 Jump Sequencing Request Process The Jump Sequencing Request
		 * Subprocess identifies an activity to deliver and assumes the current
		 * sequencing session has already begun. If the sequencing session has
		 * begun and Jump Sequencing Request Subprocess will be invoked from the
		 * current activity, and result in the identification of the target.
		 * This activity tree traversal is not affected by any control modes –
		 * the target activity will be identified for delivery.
		 * 
		 * Nota: Per eseguire correttamente il test CM-03a è necessario
		 * utilizzare la seconda interpretazione
		 */
		if (!this.current){
			return {activity: null, exception: "SB.2.13-1"};
		}
		return {activity: target, exception: null}
	},
	
	/* Flow Subprocess [SB.2.3] */
	flowSubprocess: function(node, direction, children, dontTerminate) {
		var fttsp = this.flowTreeTraversalSubprocess(node, direction, null, children, dontTerminate);
		if (fttsp.activity == null) {
			return {activity: node, deliverable: false, endSs: fttsp.endSs, exception: fttsp.exception};
		}else{
			var fats = this.flowActivityTraversalSubprocess(fttsp.activity, direction, null); 
			return {
				activity: fats.activity,
				deliverable: fats.deliverable,
				endSs: fats.endSs,
				exception: fats.exception,
			};
		}
	},
	
	/* Choice Flow Subprocess [SB.2.9.1] */
	choiceFlowSubprocess: function(node,direction){
		var act = this.choiceFlowTreeTraversalSubprocess(node,direction);
		if(act==null){
			return {activity: node};
		}else{
			return {activity: act};
		}
	},
	
	/* Choice Flow Tree Traversal Subprocess [SB.2.9.2] */
	choiceFlowTreeTraversalSubprocess: function(node,direction){
		/* 1 */
		if (direction == 'forward'){
			if(this.preorder.getLast() == node
					|| this.root == node){
				return null;
			}
			if (node.owner.availableChildren().getLast()==node){
				return this.choiceFlowTreeTraversalSubprocess(node.owner,'forward');
			}else{
				return node.next();
			}
		}
		/* 2 */
		if (direction == 'backward'){
			if (node == this.root){
				return null;
			}
			if (node==node.owner.availableChildren()[0]){
				return this.choiceFlowTreeTraversalSubprocess(node.owner,'backward');
			}else{
				return node.prev();
			}
		}
	},
	
	/* Choice Activity Traversal Subprocess [SB.2.4] */
	choiceActivityTraversalSubprocess: function(node,direction){
		if(direction=='forward'){
			var srcp = new RollupProcess(node,this).sequencingRulesCheckProcess(node, 'stopforwardtraversal');
			if (srcp != null){
				return {deliverable: false, exception: 'SB.2.4-1'};
			}
			return {deliverable: true, exception: null};
		}
		if (direction == 'backward'){
			if (node.owner){
				if (node.owner.getControlMode().forwardOnly== true){
					return {deliverable: false, exception: 'SB.2.4-2'};
				}
			}else{
				return {deliverable: false, exception: 'SB.2.4-3'};
			}
			return {deliverable: true, exception: null};
		}
	},
	
	/* Flow Activity Traversal Subprocess [SB.2.2] */
	flowActivityTraversalSubprocess: function(node,direction,previous){
		if(node.owner && node.owner.getControlMode().flow == false){
			return {
				activity: node,
				deliverable: false,
				exception: "SB.2.2-1",
			};
		}
		/*
		 * Apply the Sequencing Rules Check Process to the activity and its
		 * Skipped sequencing rules
		 */
		/* 2 */
		var srcp = new RollupProcess(node,this).sequencingRulesCheckProcess(node,["skip"]);
		/* 3 */
		if (srcp!=null){
			var ftts = this.flowTreeTraversalSubprocess(node, direction, previous, false)
			if(ftts.activity==null){
				return {
					activity: node,
					deliverable: false,
					endSs: ftts.endSs,
					exception: ftts.exception,
				};
			/* 3.3 */
			}else{
				var fats;
				if (previous=="backward" && ftts.direction=="backward"){
					/*
					 * Apply the Flow Activity Traversal Subprocess to the
					 * activity identified by the Flow Tree Traversal Subprocess
					 * in the traversal direction and a previous traversal
					 * direction of n/a
					 * 
					 * Non è chiaro ma così funziona
					 */
					fats = this.flowActivityTraversalSubprocess(ftts.activity,ftts.direction,null);
				}else{
					fats = this.flowActivityTraversalSubprocess(ftts.activity,direction,previous);
				}
				return fats;
			}
		}
		/*
		 * Apply the Check Activity Process to the activity 5
		 */
		if (this.checkActivityProcess(node)==true){
			return {
				deliverable: false,
				activity: node,
				exception: "SB.2.2-2",
			};
		}
		/* 6 */
		if (node.nodes){
			var ftts = this.flowTreeTraversalSubprocess(node, direction, null, true);
			if (ftts.activity == null){
				return {
					deliverable: false,
					activity: node,
					endSs: ftts.endSs,
					exception: ftts.exception
				};
			}else{
				/* 6.3.1 */
				var fats;
				if (direction == "backward" && ftts.direction == "forward"){
					fats  = this.flowActivityTraversalSubprocess(ftts.activity,"forward","backward")
				}else{
					fats  = this.flowActivityTraversalSubprocess(ftts.activity,direction,null)
				}
				/* 6.3.3 */
				return fats;
			}
		}
		return {
			deliverable: true,
			activity: node,
			exception: null
		};
	},
	
	/* Check Activity Process [UP.5] */
	checkActivityProcess: function(node){
		/* 2 */
		if (new RollupProcess(node,this).sequencingRulesCheckProcess(node,["disabled"])!=null){
			return true;
		}
		/* 4 */ 
		return (this.limitConditionsCheckProcess(node)==true) ? true : false;
	},
	
	/* Limit Conditions Check Process [UP.1] */
	limitConditionsCheckProcess: function(node){
		/*
		 * For an activity; returns True if any of the activity’s limit
		 * conditions have been violated
		 */
		/* 1 */
		if(node.options.data.item.sequencing.deliveryControls
				&& node.options.data.item.sequencing.deliveryControls.tracked==false){
			return false;
		}
		/* 2 controlare condizione */
		if(node.options.data.track.activityIsActive==true || node.options.data.track.getActivityIsSuspended()==true){
			return false;
		}
		/* 3 */
		if (node.options.data.item.sequencing
				&& node.options.data.item.sequencing.limitConditions
				&& node.options.data.item.sequencing.limitConditions.attemptLimit){
			if(node.options.data.track.activityProgressStatus == true
					&& node.options.data.track.activityAttemptCount >=
						node.options.data.item.sequencing.limitConditions.attemptLimit){
				return true;
			}
		}
		/*
		 * 4-9 controllo sui tempi massimi per attempt [opzionale]
		 */
		/* 10 */
		return false;
	},
	
	/* Flow Tree Traversal Subprocess [SB.2.1] */
	flowTreeTraversalSubprocess: function(node, direction, previous, children, dontTerminate){
		/*
		 * For an activity, a traversal direction, a consider children flag, and
		 * a previous traversal direction; returns the ‘next’ activity in
		 * directed traversal of the activity tree, may return the traversal
		 * direction, may indicate control be returned to the LTS; and may
		 * return an exception code:
		 */
		
		var revDirection = false; /* 1 */
	 
		var parentAvailableChildren = node.owner.availableChildren();
		
		/* 2 */
		if ((previous && previous=="backward") && node && node.owner && parentAvailableChildren.getLast()==node){
			direction = "backward";
			/*
			 * activity (node) is the first activity in the activity’s parent’s
			 * list of Available Children (2.2)
			 */
			parentAvailableChildren.erase(node);
			parentAvailableChildren.unshift(node);
			
			revDirection = true;
		}
		/* 3 */
		if (direction && direction=="forward"){
			/* 3.1 */
			if (this.preorder.getLast()==node || (node == this.root && children==false)){
				if (!dontTerminate){
					this.terminateDescendentAttemptsProcess(this.root);
				}
				return {
					activity: null,
					endSs: true,
					exception: null
				};	
			} 
			/* 3.2 */
			if (!node.nodes || children == false){
				if (node.owner && parentAvailableChildren.getLast()==node){
					return this.flowTreeTraversalSubprocess(node.owner, "forward", null, false);
				}else{
					var n=node.next();// parentAvailableChildren[parentAvailableChildren.indexOf(node)+1];
					return {
						activity: n,
						direction: direction,
						exception: null
					};
				}
			/* 3.3 */
			}else{
				if (node.availableChildren().length>0){
					return {
						activity: node.availableChildren()[0],
						direction: direction,
						exception: null
					};
				}else{
					return {
						activity: null,
						direction: null,
						exception: "SB.2.1-2"
					};
				}
			}
		}
		/* 4 */
		if (direction && direction=="backward"){
			/* 4.1 */
			if(node==this.root){
				return {
					activity: null,
					direction: null,
					exception: "SB.2.1-3"
				};
			}
			/* 4.2 */
			if (!node.nodes || children==false){
				if(revDirection == false){
					if(node.owner.getControlMode().forwardOnly == true){
						return {
							activity: null,
							direction: null,
							exception: "SB.2.1-4"
						};
					}
				}
				/* 4.2.2 */
				if(parentAvailableChildren[0]==node){
					return this.flowTreeTraversalSubprocess(node.owner, "backward", null, false);
				}else{
					return {
						activity: node.prev(),
						direction: direction,
						exception: null
					};
				}
			}else{
				/* 4.3.1 */
				if (node.availableChildren().length>0){
					if(node.getControlMode().forwardOnly==true){
						return {
							activity: node.availableChildren()[0], 
							direction: "forward",
							exception: null
						};
					}else{
						return {
							activity: node.availableChildren().getLast(),
							direction: "backward",
							exception: null
						};
					}
				}else{
					return {
						activity: null,
						direction: null,
						exception: "SB.2.1-2"
					};
				}
			}
		}
	},
	
	/* Sequencing Post Condition Rules Subprocess [TB.2.2] */
	sequencingPostConditionRuleSubprocess: function(node){
		/* 1 */
		if (this.current.options.data.track.getActivityIsSuspended() == true ){
			return {};
		}
		var v = !!node.options.data.item
				.sequencing.sequencingRules?node.options.data.item
				.sequencing.sequencingRules.postConditionRule.map(function(pcr){
			return pcr.ruleAction.action.toLowerCase();
		}):[];
		
//		V=['retry','retryall','exitparent','exitall','continue','previous'];
		
		var srcp = new RollupProcess(node,this).sequencingRulesCheckProcess(node,v);
		if (srcp!= null){
			if (['retry','continue','previous'].contains(srcp.toLowerCase())){
				return {terminationRequest: null, sequencingRequest: srcp}
			}
			if (['exitparent','exit_parent','exitall','exit_all'].contains(srcp.toLowerCase())){
				return {terminationRequest: srcp, sequencingRequest: null}
			}
			if (srcp.toLowerCase()=='retryall'){
				return {terminationRequest: 'exitall', sequencingRequest: 'retry'}
			}
		}
		return {terminationRequest: null, sequencingRequest: null}
	},
	
	/* Sequencing Exit Action Rules Subprocess [TB.2.1] */
	sequencingExitActionRulesSubprocess: function(){
		var actPath = this.current.pathFromRoot();
		var target = null;
		/* 3 */ 
		actPath.some(function(node){
			if(new RollupProcess(this.current, this).sequencingRulesCheckProcess(node, ['exit'])!=null){
				target = node;
				return true;
			}else return false;
		},this);
		
		/* 4 */
		if (target){
			this.terminateDescendentAttemptsProcess(target);
			this.endAttemptProcess(target);
			this.current = target;
		}
	},
	
	/* Termination Request Process [TB.2.3] */
	terminationRequestProcess: function(request){
		var temp = {
				valid: false,
				sequencingRequest: null,
				exception: null
			}
		if (!this.current){
			temp.exception = "TB.2.3-1";
			return temp;
		}
		if ((request=='exit' || request == 'abandon')
				&& this.current.options.data.track.activityIsActive == false){
			temp.exception = 'TB.2.3-2';
			return temp;
		}
		switch (request){
			case 'exit':
				this.endAttemptProcess(this.current);
				this.sequencingExitActionRulesSubprocess();
				do{
					var processedExit = false;
					var spcrs = this.sequencingPostConditionRuleSubprocess(this.current);
					/* 3.3.3 */
					if (spcrs.terminationRequest && ['exitall','exit_all'].contains(spcrs.terminationRequest.toLowerCase())){
						return this.terminationRequestProcess('exitall');
						break;
					}
					/* 3.3.4 */
					if (spcrs.terminationRequest && ['exitparent','exit_parent'].contains(spcrs.terminationRequest.toLowerCase())){
						if (this.current != this.root){
							this.current = this.current.owner;
							this.endAttemptProcess(this.current);
							processedExit = true;
						}else{
							temp.exception='TB.2.3-4';
							return temp;
						}
					}else{
						if (this.current == this.root && spcrs.terminationRequest && spcrs.sequencingRequest.toLowerCase() != 'retry'){
							temp.valid = true;
							temp.sequencingRequest = "exit";
							return temp;
						}
					}
				}while(processedExit != false);
				temp.valid=true;
				temp.sequencingRequest = spcrs.sequencingRequest;
				return temp;
			break;
			/* case 4 */
			case 'exitall':
				if (this.current.options.data.track.activityIsActive == true){
					this.endAttemptProcess(this.current);
				}
				this.terminateDescendentAttemptsProcess(this.root);
				this.endAttemptProcess(this.root);
				this.current = this.root;
				temp.valid =true;
				temp.sequencingRequest = 'exit';
				return temp;
			break;
			/* case 5 */
			case 'suspendall':
				if (this.current.options.data.track.activityIsActive == true
						|| this.current.options.data.track.getActivityIsSuspended() == true){
					/*
					 * OverallRollupProcess TODO: completare implementazione
					 */
					this.suspendedActivity = this.current;
				}else{
					if(this.current != this.root){
						this.suspendedActivity = this.current.owner;
					}else{
						temp.exception = 'TB.2.3-3';
						return temp;
					}
				}
				var actPath = this.suspendedActivity.pathFromRoot().reverse();
				if (actPath.length == 0){
					temp.exception = 'TB.2.3-5';
					return temp;
				}
				actPath.each(function(node){
					node.options.data.track.activityIsActive = false;
					node.options.data.track.setActivityIsSuspended(true);
				});
				this.current = this.root;
				temp.valid = true;
				temp.sequencingRequest = 'exit';
				return temp;
			break;
			/* case 6 */
			case 'abandon':
				this.current.options.data.track.activityIsActive = false;
				temp.valid = true;
				return temp;
			break;
			/* case 7 */
			case 'abandonall':
				var actPath = this.current.pathFromRoot().reverse();
				if (actPath.length == 0){
					temp.exception = 'TB.2.3-6';
					return temp;
				}
				actPath.each(function(node){
					node.options.data.track.activityIsActive = false;
				});
				this.current = this.root;
				temp.valid = true;
				temp. sequencingRequest = 'exit';
				return temp;
			break;
		}
		temp.exception = 'TB.2.3-7';
		return temp;
	},
	
	/* End Attempt Process [UP.4] */
	endAttemptProcess: function(node){
		this.saveFromCmi(node);
		/* 1 */
		if (!node.nodes){
			if(node.options.data.item.sequencing.deliveryControls.tracked == true){
				if (node.options.data.item.sequencing.deliveryControls.completionSetByContent==false
						&& node.options.data.track.getAttemptProgressStatus()==false){
					node.options.data.track.getAttemptProgressStatus()==true;
					node.options.data.track.setAttemptCompletionStatus(true);
				}
				if (node.options.data.item.sequencing.deliveryControls.objectiveSetByContent==false){
					at.objectives.each(function(obj){
						if (obj.node == node
								&& obj.primaryObj==true
								&& obj.getObjectiveProgressStatus() == false){
							obj.setObjectiveProgressStatus(true);
							obj.setObjectiveSatisfiedStatus(true);
						}
					});
					
				}
			}
		/* 2 */
		}else{
			if (node.nodes.some(function (n){
				return n.options.data.track.getActivityIsSuspended();
			})){
				node.options.data.track.setActivityIsSuspended(true);
			}else{
				node.options.data.track.setActivityIsSuspended(false);
			}
		}
		/* 3 4 */
		node.options.data.track.activityIsActive = false;
		
		new RollupProcess(node,this).overallRollupProcess();
		
		node.deselectNode();
		console.debug("Attempt end: " + node.options.label);
		if (!node.options.enabled && !node.options.data.track.activityIsActive){
			node.disable();
		}
//		API_1484_11 = null;
		$('scormFrame').src = null;
	},
	
	/* Terminate Descendent Attempts Process [UP.3] */
	terminateDescendentAttemptsProcess: function(node){
		var nodePath = node.pathFromRoot().reverse();
		
		var curPath = this.current.pathFromRoot().reverse();
	
		var ancestor = curPath.pop();
		nodePath.pop();
		while(curPath.length && nodePath.length 
				&& curPath.getLast()==nodePath.getLast()){
			ancestor = curPath.pop();
			nodePath.pop();
		}
		
		var actPath = curPath.slice(1);
		if (actPath.length > 0){
			actPath.each(function(n){
				this.endAttemptProcess(n);
			},this);
		}
	},
	
	/* Overall Sequencing Process [OP.1] */
	overallSequencingProcess: function(request,target){
		at.clearDebounce.delay(1000);
		
		var pendingRequest;
		
		/*
		 * se target non è definito prova a cercarlo nella richiesta secondo la
		 * sintassi definita dallo scorm
		 */
		if (!target && (m = request.match(/{target=(.*)}(choice|jump)/))){
			request = m[2];
			target = m[1];
		}
		var nr = navigationRequest[request.toLowerCase()].bind(this)(target);
		
		if (nr.valid==false){
			/* handle exception */
			console.error('Sequencing request process exception: ',nr.exception);
			return;
		}
		target=nr.target;
		if (nr.terminationRequest){
			var tr = this.terminationRequestProcess(nr.terminationRequest.toLowerCase());
			if (!tr.valid){
				/* handle exception */
				console.error('Sequencing request process exception: ',tr.exception);
				return;
			}
			if (tr.sequencingRequest){
				nr.sequencingRequest = tr.sequencingRequest;
			}
		}
		var delivery;
		/* 1.4 */
		if (null != nr.sequencingRequest){
			var sr = this.sequencingRequestProcess(nr.sequencingRequest.toLowerCase(),target);
			if (!sr.valid){
				/* handle exception */
				console.error('Sequencing request process exception: ',sr.exception);
				return;
			}
			/* 1.4.3 */
			/*
			 * If the Sequencing Request Process returned a request to end the
			 * sequencing session Then
			 */
			if (sr.endSs == true){
				// debug
// if (confirm("vuoi veramente uscire?")){
// window.close();
// }else{
// location.reload();
// }
				console.info("End sequencing session");
				return;
				/*
				 * Exit Overall Sequencing Process - the sequencing session has
				 * terminated; return control to LTS
				 */
			}
			/* 1.4.4 */
			if (sr.activity == null){
				return
			}
			delivery = sr.activity;
		}
		/* 1.5 */
		if (delivery){
			var drp = this.deliveryRequestProcess(delivery);
			if (false == drp.valid){
				console.log(drp);
				return;
			}
			this.contentDeliveryEnvironmentProcess(delivery);
		}
	},
	
	/* Content Delivery Environment Process [DB.2] */
	contentDeliveryEnvironmentProcess: function(delivery){
		/* 1 */
		if (this.current && this.current.options.data.track.activityIsActive){
			return {exception: "DB.2-1"};
		}
		/* 2 */
		if (delivery != this.suspendedActivity){
			this.clearSuspendedActivitySubprocess(delivery);
		}
		/* 3 */
		if (this.current)
			this.terminateDescendentAttemptsProcess(delivery);
		
		/* 4 5 */
		delivery.pathFromRoot().each(function(node){
			if (node.options.data.track.activityIsActive == false){
				if (node.options.data.item.sequencing.deliveryControls.tracked == true){
					if (node.options.data.track.getActivityIsSuspended() == true){
						node.options.data.track.setActivityIsSuspended(false);
					}else{
						node.options.data.track.activityAttemptCount += 1;
						node.options.data.track.newAttempt();
						if (node.options.data.track.activityAttemptCount == 1){
							node.options.data.track.activityProgressStatus = true;
						}
						/*
						 * 5.1.1.2.3. Initialize Objective Progress Information
						 * and Attempt Progress Information required for the new
						 * attempt. Dovrebbe essere già fatto alla creazione
						 * dell'oggeto track
						 */
					}
				}
				node.options.data.track.activityIsActive = true;
			}
		});
		/* 6 7 */
		this.current = delivery;
		this.suspendedActivity = null;
		
		/* 8 */
		/*
		 * Once the delivery of the activity’s content resources and auxiliary
		 * resources begins The delivery environment is assumed to deliver the
		 * content resources associated with the identified activity. While the
		 * activity is assumed to be active, the sequencer may track learner
		 * status.
		 */
		API_1484_11 = new API(this,delivery, this.organization.sharedDataGlobalToSystem);
		this.saveToCmi(delivery);
		delivery.selectNode();
		var scoUrl = this.scormPlayer.resources[delivery.options.data.item.identifierref]+delivery.options.data.item.parameters;
		$("scormMain").set('html', '<iframe id="scormFrame" name="scormFrame" src="' + scoUrl + '" width="100%" height="100%" />');
		
		this.scormPlayer.layout.buttons.each(function(btn){
			btn.domObj.setStyles({});
			btn.setEnabled(true);
		})
		if (delivery.options.data.item.presentation && delivery.options.data.item.presentation.navigationInterface){
			delivery.options.data.item.presentation.navigationInterface.hideLMSUI.each(function(device){
				this.scormPlayer.layout.buttons[device.toLowerCase()+"Btn"].domObj.fade('out');
			},this)
		}
		
		// REQ_117.2 && REQ_117.4
		if(this.sequencingRequestProcess("continue",null,true).valid==false){
			this.scormPlayer.layout.buttons.continueBtn.setEnabled(false);
		}
		
		if(this.sequencingRequestProcess("previous",null,true).valid==false){
			this.scormPlayer.layout.buttons.previousBtn.setEnabled(false);
		}
		
		function recHideAndDisable(node,at,rec,rec2){
			node.enable()
			var descend = false;
			var descend2 = false;
			var rp = new RollupProcess(node,at);
// var nr = navigationRequest["choice"].bind(at)(node);
// var nr = at.choiceSequencingRequestProcess(delivery);
			var current = at.current;
			var nr = at.sequencingRequestProcess('choice',node,true);
			at.current = current;
			
			if(nr.valid==false ){
// if(nr.exception!=null){
				node.disable();
			}
			
			if (rp.sequencingRulesCheckProcess(node,['disabled'])|| rec){
				node.disable();
				descend = true
			}
			
			if (node != delivery 
					&& !!node.options.data.item.sequencing.limitConditions 
					&& !!node.options.data.item.sequencing.limitConditions.attemptLimit 
					&& node.options.data.track.activityAttemptCount >= node.options.data.item.sequencing.limitConditions.attemptLimit || rec2){
				node.disable();
				descend2 = true;
			}
			
			if (node.nodes){
				node.nodes.each(function(n){
					recHideAndDisable(n, at, descend, descend2);
				})
			}
			var f = new Fx.Slide(node.domObj);
			f.show();
			
			if (rp.sequencingRulesCheckProcess(node,['hiddenfromchoice'])){
				new Fx.Slide(node.domObj).hide();
			}
			/*
			 * isvisible (optional): The isvisible attribute indicates whether
			 * or not this item is displayed when the structure of the package
			 * is displayed or rendered. If not present, value is defaulted to
			 * be true [3]. The value only affects the item for which it is
			 * defined and not the children of the item or a resource associated
			 * with an item. XML Data Type: xs:boolean.
			 */
			if (node.options.data.item.isvisible == false){
				new Fx.Slide(node.domLabel.getParent()).hide();
			}
		}
		
		
		
		
		recHideAndDisable(this.root,this);
		
		var path = delivery.pathFromRoot().reverse();
		path.each(function(n){
			if (n.getControlMode().choiceExit==false){
				n.owner.nodes.each(function(m){
					if (m!=n){
						new Fx.Slide(m.domObj).hide()
					}
				})
			}
		});

		if (delivery.options.data.item.sequencing.deliveryControls.tracked == false){
			/*
			 * The Objective and Attempt Progress information for the activity
			 * should not be recorded during delivery
			 * 
			 * The delivery environment begins tracking the Attempt Absolute
			 * Duration and the Attempt Experienced Duration
			 */
		}
		return {exception: null};
	},
	
	/* Clear Suspended Activity Subprocess [DB.2.1] */
	clearSuspendedActivitySubprocess: function (node){
		if(this.suspendedActivity){
			var nodePath = node.pathFromRoot();
			var suspPath = this.suspendedActivity.pathFromRoot();
			var ancestor = suspPath.pop();
			nodePath.pop();
			nodePath.some(function(n,i){
				if (nodePath[i]==suspPath[0]){
					ancestor = suspPath.pop();
					return false;
				}else{
					return true;
				}
			});

			new Array([ancestor]).extend(suspPath).flatten().each(function(n){
				if (!n.nodes){
					n.options.data.track.setActivityIsSuspended(false);
				}else{
					if (!n.nodes.some(function(m){
						return m.options.data.track.getActivityIsSuspended(); 
					})){
						n.options.data.track.setActivityIsSuspended(false);
					}
				}
			});
			this.suspendedActivity = null;
		}
	},
	
	/* Delivery Request Process [DB.1.1] */
	deliveryRequestProcess: function(delivery){
		/* 1 */
		if (delivery.nodes){
			return {
				valid: false,
				exception: "DB.1.1-1",
			};
		}
		var actPath = delivery.pathFromRoot();
		
		if (actPath.length == 0){
			return {
				valid: false,
				exception: "DB.1.1-2",
			};
		}
		var toReturn = {valid: true, exception: null};
		
		actPath.some(function(node){
			if (this.checkActivityProcess(node)){
				toReturn.valid = false;
				toReturn.exception = "DB.1.1-3";
				return true;
			}else{
				return false;
			}
		},this);
		return toReturn;
	},
	
	/* Sequencing Request Process [SB.2.12] */
	sequencingRequestProcess: function(request,target,dontTerminate){
		var srp = this[request+"SequencingRequestProcess"](target,dontTerminate);
		if (srp.exception){
			return {valid: false, activity: null, endSs: null, exception: srp.exception};
		}else{
			return {valid: true, activity: srp.activity, endSs: srp.endSs, exception: null};
		}
	},
	
	
	/* Randomize Children Process [SR.2] */
	randomizeChildrenProcess: function(node){
		if (!node.nodes){
			return;
		}
		if (node.options.data.track.getActivityIsSuspended() == true
				|| node.options.data.track.activityIsActive == true){
			return;
		}
		var rc = node.options.data.item.sequencing.randomizationControls
		switch (rc.randomizationTiming.toLowerCase()){
			case 'never': break;
			case 'once' : 
				if (node.options.data.item.track.activityProgressStatus == false){
					if (rc.randomizeChildren == true){
						node.ac = node.availableChildren().shuffle();
					}
				}
				break;
			case 'oneachnewattempt':
				if (rc.randomizeChuildren== true){
					node.ac = node.availableChildren().shuffle();
				}
				break;
		}
		return;
	},
	/* fine funzioni di sequencing */
	
	/*
	 * Continue, Previous, Suspend: funzioni da colelgare ai pulsanti
	 * corrispettivi che fanno la rispettiva richiesta all'Overall Sequencing
	 * Process
	 */
	Continue: function() {
		if (!this.debounce){
			this.debounce = true;
			$('scormHeader').setStyle('background-color','red');
			this.overallSequencingProcess('continue');
			$('scormHeader').setStyle('background-color','grey');
		}
	},
	
	Previous: function() {
		if (!this.debounce){
			this.debounce = true;
			$('scormHeader').setStyle('background-color','red');
			this.overallSequencingProcess('previous');
			$('scormHeader').setStyle('background-color','grey');
		}
	},
	
	SuspendAll: function() {
		if (!this.debounce){
			this.debounce = true;
			this.overallSequencingProcess('suspendall');
			this.debounce = false;
		}
	},
	
	ExitAll: function() {
		if (!this.debounce){
			this.debounce = true;
			this.overallSequencingProcess('exitall');
			this.debounce = false;
			window.close();
		}
	},

	Exit: function() {
		if (!this.debounce){
			this.debounce = true;
			this.overallSequencingProcess('exit');
			this.debounce = false;
		}
	},
	
	Abandon: function() {
		if (!this.debounce){
			this.debounce = true;
			this.overallSequencingProcess('abandon');
			this.debounce = false;
		}
	},
	
	AbandonAll: function() {
		if (!this.debounce){
			this.debounce = true;
			this.overallSequencingProcess('abandonall');
			this.debounce = false;
			window.close();
		}
	},
	/*
	 * Crea l'activity tree a partire dal manifest, crea lo ScormTreeFolder che
	 * rappresenta l'organization e root dell'activity tree, chiama la funzione
	 * addItem() su tutte le attività figle della root che crerà in modo
	 * ricorsivo l'albero
	 */
	makeTree: function() {
		defaulter(this.organization, this.seqCollection);
		
    	var options = {
    			label: this.organization.title,
    			open:true
    	};
		
		var node = new ScormTreeFolder(options);
		
		this.organization.item.each(function(item,index) {
			node.addItem(item,this,index, this.seqCollection);
		},this);
		
		at=this;
		var options = {
				onClick: function(node){
					if (this.options.enabled){
						at.overallSequencingProcess('choice',node);
					}
				},
				data: {
					index:this.index,
					item:this.organization,
					play: new Element('img',{
						src: contextPath + ".resources/lms/icons/play.png"
					}),
					pause: new Element('img',{
						src: contextPath + ".resources/lms/icons/pause.png"
					}),
				},
		};
		
    	node.setOptions(options);
    	node.options.data.track = new TrackingModel(node,this);
		this.root = node;
	},

	/*
	 * funzione che inizializza parte del data model (gli obiettivi) di
	 * un'attività prima che questa venga attivata
	 * 
	 * .concat([node.options.data.item.sequencing.objectives.primaryObjective])
	 */ 
	saveToCmi: function(node) {
		if(node.options.data.item.sequencing && node.options.data.item.sequencing.objectives){
			node.options.data.item.sequencing.objectives.objective.concat([node.options.data.item.sequencing.objectives.primaryObjective]).each(function(obj) {
				if (obj.objectiveID!=""){
					var c = API_1484_11.s.get("cmi.objectives._count");
					API_1484_11.s.set("cmi.objectives."+c+".id",obj.objectiveID);
					var v;
					if (this.objectives[obj.objectiveID]){
						v = this.objectives[obj.objectiveID].getObjectiveSatisfiedStatus();
						if (this.objectives[obj.objectiveID].getObjectiveSatisfiedStatus()== true){
							v = "passed";
						}
						if (this.objectives[obj.objectiveID].getObjectiveSatisfiedStatus()== false){
							v = "failed";
						}
					}else {
						v = "unknown";
					}
					API_1484_11.s.set("cmi.objectives."+c+".success_status", v);
					
					API_1484_11.s.set("cmi.objectives."+c+".score.scaled", this.objectives[obj.objectiveID].getObjectiveNormalizedMeasure());
				}
			},this);
		}
	},
	
	/*
	 * funzione che aggiorna gli obiettivi del tracking model a partire da
	 * quelli salvati nel tracking model quando un'attività viene terminata
	 * 
	 * controllare Table 4.5.4a: Run-Time Data to Sequencing Tracking Data Mapping Summary SN
	 */
	saveFromCmi: function(node) {
		var s=API_1484_11._getStore();
		s.cmi.objectives.array.each(function(obj){
			if (!this.objectives.has(obj.id)){
				this.objectives.set(obj.id,new Objective({objectiveID:obj.id,mapInfo: new Hash()},node,this));
			}
			this.objectives[obj.id].update(obj,this);
		},this);
		this.current.options.data.track.update(s);
	}
});