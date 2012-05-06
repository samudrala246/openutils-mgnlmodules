function check(toCheck){
	var arr=toCheck.split(".");
	var toEval=arr[0];
	return eval(toEval) && arr.slice(1,arr.length).every(function(i){
		toEval+="."+i;
		alert(toEval);
		return !!eval(toEval)
	});
}
 
Array.implement('count',function(fn){
	var c = 0;
	this.each(function(item) {
		if (fn(item)) c++;
	});
	return c;
});



function init(manifest, context) {

	var current;
	var next;
	var objectives= new Hash();
	
	var Attempt = new Class({
		Implements: [Options],
		options:{
			started: false,
			activity: null,
		},
		initialize: function(options) {
			this.setOptions(options);
			this.options.play=new Element('img',{
				src:".resources/mgnllms/icons/play.png"
			});
		},
		start: function(node) {
			this.options.started=true;
			node.domObj.getElement('a').grab(this.options.play);
		},
		end: function() {
			this.options.play.dispose();
			this.options.started=false;
		},
		
	});	
	
	var navRequests = new Hash({
		Continue: function() {
			if (current){
				terminationRequests.Exit();
				sequencingRequests.Continue();
			}
			if (current && current.owner){
				var i = current.owner.nodes.indexOf(current);
				if(++i==current.owner.nodes.length){
					current.deselectNode();
					current=current.owner;
					navRequests.Continue();
				}
				else
					current.owner.nodes[i].selected(null,true);
			}else{
				if (current)
					$('scormFrame').src = ".resources/mgnllms/termine.html";
					current=null;
			}
		},
		Previous: function() {
			if (current){
				terminationRequests.Exit();
				sequencingRequests.Previous();
			}
			if (curren && current.owner ){
				var i = current.owner.nodes.indexOf(current);
				current.owner.nodes[i-1].selected(null,true);
			}
		},
		Start: function(){},
		ResumeAll: function(){},
		Choice: function(){},
		Forward: function(){/* Not specified in this version of SCORM. */},
		Backward: function(){/* Not specified in this version of SCORM. */},
		Jump: function(){},
		Exit: function(){},
		ExitAll: function(){},
		SuspendAll: function(){},
		Abandon: function(){},
		AbandonAll: function(){},		
	});
	
	var terminationRequests = new Hash({
		Exit: function() {	},
		ExitParent: function() {
			current=current.owner;
			navRequests.Continue();
		},
		ExitAll: function() {
			while (!current.options.data.organization){
				current=current.owner;
			}
			navRequests.Continue();
		},
		SuspendAll: function() {	},
		Abandon: function() {	},
		AbandonAll: function() {	},
	});
	
	var sequencingRequests = new Hash({
		Continue: function() {
			if (current && !(current.owner && current.owner.owner && !current.owner.getControlMode().flow)) // SN
																											// 209
				flow_sp(current,"forward");
		},
		Previous: function() {
			if(current && !(current.owner && current.owner.owner && !current.owner.getControlMode().flow))
				flow_sp(current,"backward");
		},
		Choice: function() {
			
		}
	});
	
	function flow_sp(current, direction) {
		
	}

	var seqCondition = new Class({
	  
		initialize: function(node){
			this.node=node;
		},
		
	    SATISFIED: function(rc) {
	      return objectives[rc.referencedObjective].objectiveSatisfiedStatus},
	    OBJECTIVE_STATUS_KNOWN: function(rc) {
	      return objectives[rc.referencedObjective].objectiveProgressStatus},
	    OBJECTIVE_MEASURE_KNOWN: function(rc) {
	      return objectives[rc.referencedObjective].objectiveMeasureStatus},
	    OBJECTIVE_MEASURE_GREATER_THAN: function(rc) {
	      return objectives[rc.referencedObjective].objectiveMeasureStatus &&
	        objectives[rc.referencedObjective].objectiveNormalizedMeasure > rc.measureTreshold},
	    OBJECTIVE_MEASURE_LESS_THAN: function(rc) {
	      return objectives[rc.referencedObjective].objectiveMeasureStatus &&
	        objectives[rc.referencedObjective].objectiveNormalizedMeasure < rc.measureTreshold},
	    COMPLETED: function(rc) {
	      return objectives[rc.referencedObjective].attemptProgressStatus &&
	        objectives[rc.referencedObjective].attemptCompletionStatus;	},
	    ACTIVITY_PROGRESS_KNOWN: function(rc) {
	      return objectives[rc.referencedObjective].activityProgressStatus &&
	        objectives[rc.referencedObjective].attemptProgressStatus; },
	    ALWAYS: function(rc) {return true},
	    ATTEMPTED: function(rc){	return !!node.options.data.attempCount; },
	    ATTEMPTED_LIMIT_EXCEEDED: function(rc) {
	    	return objectives[rc.referencedObjective].activityProgressStatus &&
					node.options.data.attempCount >= node.options.data.item.sequencing.limitConditions.attemptLimit; }
	});

	var ruleAction= new Class({
		initialize: function(node) {
			this.node=node;
    	},
    	SKIP:  function() {
    		navRequests.Continue();
    	},
    	DISABLED: function() {
    		this.node.options.data.disabled=true;
    	},
    	HIDDEN_FROM_CHOICE: function() {
    		this.node.options.enabled=false;
    	},
    	STOP_FORWARD_TRAVERSAL: function() {

    	},
    	EXIT_PARENT: function() {
    		terminationRequests.ExitParent()
		},
		EXIT_ALL: function() {
			terminationRequests.ExitAll()
		},
		RETRY: function() {
			
		},
		RETRY_ALL: function() {
			
		},
		CONTINUE: function() {
			navRequests.Continue();
		},
		PREVIOUS: function() {
			navRequests.Previous();
		},
    });

	var base = manifest.resources.base;
	var resources = new Hash();
	manifest.resources.resource.each(function(r) {
		resources.set(r.identifier, r.href);
	});

	var IScorm = new Class({
		getControlMode: function(){
		if (this.options.data.item.sequencing)
			return this.options.data.item.sequencing.controlMode;
  		},
  		selectNode: function(){
  			this.domObj.getElement('a').addClass('jxTreeItemSelected');
  			this.domObj.removeClass('jxDisabled');
  		},
  		deselectNode: function(){
  			this.domObj.getElement('a').removeClass('jxTreeItemSelected');
  			this.options.data.attempt.end(node);
	    },
	    disableNode: function(b) {
	    	this.options.enabled=false;
	    	if (!b)
	    		this.domObj.addClass('jxDisabled');
	    	if (this.nodes)
	    		this.nodes.each(function(n) {
	    			n.disableNode(true);
	    		});
	    },
	    enableNode: function() {
	    	this.options.enabled=true;
	    	this.domObj.removeClass('jxDisabled');
	    	if (this.nodes)
	    		this.nodes.each(function(n) {
	    			n.enableNode(n);
	    		})
	    },
	});

	var ScormTreeItem = new Class({
		Extends :Jx.TreeItem,
		Implements: IScorm,
    
		selected: function(e,ignoreEn) {
			this.options.ignoreEn=ignoreEn;
			this.parent();
    	},
    	getName: function() {
    		return this.options.data.item.identifier;
    	},
	});

	var ScormTreeFolder= new Class({
		Extends: Jx.TreeFolder,
		Implements: IScorm,
		selected: function(e,ignoreEn) {
			this.options.ignoreEn=ignoreEn;
			this.parent();
    	},
    	clicked: function(e) {
    		if (!this.options.open) {
    			this.expand;
    		}
    	},
    	getName: function() {
    		return this.options.data.item.identifier;
    	},
	});
	
	var ScormTree = new Class({
		Extends: Jx.Tree,
		Implements: IScorm,
	});

	function eseguiPreConditions(node) {
		if (node.options.data.item.sequencing && node.options.data.item.sequencing.sequencingRules){
			node.options.data.item.sequencing.sequencingRules.preConditionRule.each(function(sr) {
				if(sr.ruleConditions.conditionCombination=="ALL")
					sr.ruleConditions.ruleCondition.check=sr.ruleConditions.ruleCondition.every;
				else
					sr.ruleConditions.ruleCondition.check=sr.ruleConditions.ruleCondition.some;
				var r=sr.ruleConditions.ruleCondition.check(function(rc) {
					return (rc.operator=="NOT") !=  new seqCondition(node)[rc.condition](rc);
				});
				if (r)
					new ruleAction(node)[sr.ruleAction.action]();
			});
		}
	}
	
	function eseguiPostConditions(node) {
		if (node.options.data.item.sequencing && node.options.data.item.sequencing.sequencingRules){
			node.options.data.item.sequencing.sequencingRules.postConditionRule.each(function(sr) {
				if(sr.ruleConditions.conditionCombination=="ALL")
					sr.ruleConditions.ruleCondition.check=sr.ruleConditions.ruleCondition.every;
				else
					sr.ruleConditions.ruleCondition.check=sr.ruleConditions.ruleCondition.some;
				var r=sr.ruleConditions.ruleCondition.check(function(rc) {
					return (rc.operator=="NOT") !=  new seqCondition(node)[rc.condition](rc);
				});
				if (r)
					new ruleAction(node)[sr.ruleAction.action]();
			});
		}
	}

	function scormEvent(node){
		current && current.options.data.attempt.end(node);
		if( node.getControlMode() && node.getControlMode().choiceExit){
			node.owner.nodes.each(function(n){
				if (n.options.label!=node.options.label){}
				n.disableNode();
			});
		}

		/* salva gli obiettivi dell'attivà corrente in un oggetto globale */
	    var s=API_1484_11._getStore();
	  
	    s.cmi.objectives.array.each(function(obj) {
	    	objectives.set(obj.id,new Hash({
				objectiveMeasureStatus: !!obj.score.scaled || obj.score.scaled==0,
				objectiveNormalizedMeasure: obj.score.scaled,
				objectiveProgressStatus: !(obj.success_status=="unknow"),
				objectiveSatisfiedStatus: (obj.success_status=="passed"),
				attemptProgressStatus: !(obj.completion_status=="unknow"),
				attemptCompletionStatus: (obj.completion_status=="passed"),
				attemptCompletionAmount: !!obj.progress_measure ? obj.progress_measure: "unknown",
			}));
	    });
	    
	    if (node && node.options.data.item.sequencing && node.options.data.item.sequencing.objectives){
	    	objectives.include(node.options.data.item.sequencing.objectives.primaryObjective.objectiveID, new Hash({}));
		    var temp = new Hash();
			node.options.data.item.sequencing.objectives.primaryObjective.mapInfo.each(function(mi) {
				if (mi.readSatisfiedStatus){
					temp.combine({objectiveSatisfiedStatus: objectives[mi.targetObjectiveID].objectiveSatisfiedStatus});
					temp.combine({objectiveProgressStatus: objectives[mi.targetObjectiveID].objectiveProgressStatus});
				}
				if (mi.readNormalizedMeasure){
					temp.combine({objectiveNormalizedMeasure: objectives[mi.targetObjectiveID].objectiveNormalizedMeasure});
					temp.combine({objectiveMeasureStatus: objectives[mi.targetObjectiveID].objectiveMeasureStatus});
				}
				if (mi.writeSatisfiedStatus){
					objectives[mi.targetObjectiveID].objectiveSatisfiedStatus = objectives[node.options.data.item.sequencing.objectives.primaryObjective.objectiveID].objectiveSatisfiedStatus;
					objectives[mi.targetObjectiveID].objectiveProgressStatus = objectives[node.options.data.item.sequencing.objectives.primaryObjective.objectiveID].objectiveProgressStatus;
				}
				if (mi.writeNormalizedMeasure){
					objectives[mi.targetObjectiveID].objectiveNormalizedMeasure = objectives[node.options.data.item.sequencing.objectives.primaryObjective.objectiveID].objectiveNormalizedMeasure;
					objectives[mi.targetObjectiveID].objectiveMeasureStatus = objectives[node.options.data.item.sequencing.objectives.primaryObjective.objectiveID].objectiveMeasureStatus;
				}
			});
			objectives[node.options.data.item.sequencing.objectives.primaryObjective.objectiveID].combine(temp);
	    }
	    
	    if (current){
	    	
	    	current.deselectNode();
	    }
	    current=node;
	    node.selectNode();
	    previousBtn.setEnabled(current.owner.nodes.indexOf(current)&& current.owner.getControlMode() && !current.owner.getControlMode().forwardOnly);

	    if (node.getControlMode() && node.getControlMode().flow && node.nodes){
	    	node.nodes[0].selected(null,true);
	    }
	}

	function selectItem(node) {
		eseguiPreConditions(node);
		if (current==node && !node.options.data.dontLoad){
			API_1484_11 = new API();
			node.options.data.attemptCount++;
			
			$('scormFrame').src = context + base + resources[node.options.data.item.identifierref];
			node.options.data.attempt.start(node);
			/* aggiunta degli obiettivi a cmi.objectives.n */

			if(node.options.data.item.sequencing.objectives){
				node.options.data.item.sequencing.objectives.objective.each(function(obj) {
					var c = API_1484_11.s.get("cmi.objectives._count");
					API_1484_11.s.set("cmi.objectives."+c+".id",obj.objectiveID);
					// API_1484_11.s.set("cmi.objectives."+c+".succes_status",obj.)
					// if (obj.mapInfo)
					// obj.mapInfo.each(function(mi) {
					//							
					// });
				});
				var c = API_1484_11.s.get("cmi.objectives._count");
				API_1484_11.s.set("cmi.objectives."+c+".id",node.options.data.item.sequencing.objectives.primaryObjective.objectiveID);
			}
			
			
		}
	}

	function addItem(item, ref) {
		if (item.item.length == 0){
			var i=new ScormTreeItem( {
				label : item.title,
				data: {
					item:item,
					attemptCount:0,
					attempt: new Attempt({}),
				},
				enabled: !ref.getControlMode() || ref.getControlMode().choice ,
				onClick : function(e) {
					
					if (!this.options.data.disabled && this.options.enabled || this.options.ignoreEn){
						scormEvent(this);
						this.options.ignoreEn=false;
						selectItem(this);
					}
				}
			});
			ref.append(i);
		}
		else {
			var f = new ScormTreeFolder( {
				open: true,
				data: {
					item:item,
					attemptCount:0,
					attempt: new Attempt({}),
				},
				onClick: function(e) {
					
					if (!this.options.data.disabled && this.options.enabled|| this.options.ignoreEn){
						this.options.ignoreEn=false;
						scormEvent(this);
					}
				},
				label : item.title
			});
			ref.append(f);
			item.item.each(function(i) {
				addItem(i, f);
			});
		}
	}

/* layout */
	new Jx.Layout('container');

	new Jx.Layout('scormHeader', {
		height:50,
	});

	new Jx.Layout('scormTree', {
		top:50,
		width: 300
	});

	new Jx.Layout('scormMain', {
		top: 50,
		left:300,
	});

	var previousBtn = new Jx.Button({
		id: 'previousBtn',
		label: 'Previous',
		tooltip: 'Previous',
		onClick: navRequests.Previous
	}).addTo('scormHeader');

	var continueBtn = new Jx.Button({
		id: 'continueBtn',
		label: 'Continue',
		tooltip: 'Continue',
		onClick: navRequests.Continue
	}).addTo('scormHeader');

	var tree = new ScormTree({parent: 'scormTree'});
	var organizations=manifest.organizations.organization;

	organizations.each(function(org) {
		var orgTree=new ScormTreeFolder({
			label:org.title,
			open: true,
			enabled:!(org.sequencing && org.sequencing.controlMode && org.sequencing.controlMode.flow),
			data: {
				item:org,
				attemptCount:0,
				organization:true,
				attempt: new Attempt({}),
			},
			onClick: function(e){
				if (!this.options.data.disabled && this.options.enabled|| this.options.ignoreEn){
					this.options.ignoreEn=false;
					scormEvent(this);
				}
			}
		});
		org.item.each(function(item) {
			addItem(item,orgTree);
		});
		if (org.sequencing && org.sequencing.controlMode && !org.sequencing.controlMode.choice)
			orgTree.nodes.each(function(n) {
				n.disableNode();
			});
		tree.append(orgTree);

	});

	$('container').resize();

	var organizationDefault=manifest.organizations.default;
	var def;
	if(organizationDefault)
		def=tree.findChild([organizationDefault]);
	else
		def=tree.nodes[0];

	def.selected(null,true);
}