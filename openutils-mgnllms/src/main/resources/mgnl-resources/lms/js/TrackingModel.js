/* Questa classe raprpesenta le informazioni del tracking model di un'attività tranne quelle riferite agli obiettivi */

// se vengono cambiati i nomi delle variabili che persistono i
// dati nel jcr, cambiare i corrispondenti campi in
// MagnoliaLearnerActivitiesManager.java
var TrackingModel = new Class(
		{
			initialize : function(node, at) {
				this.at = at
				this.node = node;
				this.objectives = new Array();
				this.tracked = this.node.options.data.item.sequencing.deliveryControls.tracked;
				
				//puntatore all'obiettivo primario dell'attività (definito o artificiale)
				this.primaryObjective = null;
				
				this.previousAttemptInformation = new Hash();

				// se l'attività non è tracciata deve avere i valori del TM
				// settati ad
				// "unknown"

				// Activity Progress Information 4.2.1.3
				this.activityProgressStatus = "unknown";
				this.activityAbsoluteDuration = "unknown";
				this.activityExperiencedDuration = "unknown";
				this.activityAttemptCount = "unknown";

				// Attempt Progress Information 4.2.1.4
				this._attemptProgressStatus = "unknown";
				this._attemptCompletionAmount = "unknown";//
				this._attemptCompletionStatus = "unknown";//
				this.attemptAbsoluteDuration = "unknown";
				this.attemptExperiencedDuration = "unknown";

				// Activity State Information 4.2.1.5
				this.activityIsActive = "unknown";
				this._activityIsSuspended = "unknown";//
				// this.availableChildren = "unknown";

				if (this.tracked == true) {
					// Activity Progress Information 4.2.1.3
					this.activityProgressStatus = false;
					this.activityAbsoluteDuration = 0.0;
					this.activityExperiencedDuration = 0.0;
					this.activityAttemptCount = 0;

					// Attempt Progress Information 4.2.1.4
					this._attemptProgressStatus = false;
					this._attemptCompletionAmount = 0.0;
					this._attemptCompletionStatus = false;
					this.attemptAbsoluteDuration = 0.0;
					this.attemptExperiencedDuration = 0.0;

					// Activity State Information 4.2.1.5
					this.activityIsActive = false;
					this._activityIsSuspended = false;
					/* gestito all'interno delle attività */
					// this.availableChildren = this.node.nodes;
				}


				this.initializeObj();

				this.toJSON = _toJSON(
						/^(at|node|tracked|primaryObjective|caller|previousAttemptInformation)$/, this);
				this.fromJSON = _fromJSON.bind(this);
			},
			/** *********************************** */
			setAttemptProgressStatus : function(v) {
				if(v==true){
					$empty();
				}
				this._attemptProgressStatus = v;
			},
			getAttemptProgressStatus : function() {
//				if (this.node.owner
//						&& this.node!=this.at.root
//						&& this.node.owner.getControlMode().useCurrentAttemptProgressInfo == false
//						&& this._attemptProgressStatus == false){
//					return this.previousAttemptInformation._attemptProgressStatus;
//				}else{
					return this._attemptProgressStatus;
//				}
			},
			/** *********************************** */

			update : function(store) {
				// sovrascrivo le informazioni già inserite durante l'update
				// degli obiettivi solo se i valori nel 'core' sono diversi dai
				// valori di default
				if (this.tracked){
					if(store.cmi.completion_status != "unknown"){
						this._attemptProgressStatus = store.cmi.completion_status == "unknown" ? false : true;
						this._attemptCompletionStatus = store.cmi.completion_status == "completed" ? true : false;
					}
				
					if(store.cmi.progress_measure){
						this._attemptCompletionAmount = store.cmi.progress_measure != null ? parseFloat(store.cmi.progress_measure) : 0;
					}
					
					if(store.cmi.score && store.cmi.score.scaled!=null){
						this.primaryObjective._objectiveMeasureStatus = store.cmi.score && store.cmi.score.scaled != null;
						this.primaryObjective._objectiveNormalizedMeasure = store.cmi.score && store.cmi.score.scaled != null ? parseFloat(store.cmi.score.scaled) :0;
					}
					if(store.cmi.success_status!="unknown"){
						this.primaryObjective._objectiveProgressStatus = store.cmi.success_status != "unknown";
						this.primaryObjective._objectiveSatisfiedStatus = store.cmi.success_status == "passed";
					}
					
					/*
					 * When an attempt on a tracked (Tracked equals True) activity
					 * ends, “write” objective maps are honored.
					 */
					this.primaryObjective.writeObjectiveMaps();
					
					this.previousAttemptInformation.set("_attemptProgressStatus",this.getAttemptProgressStatus());
					this.previousAttemptInformation.set("_attemptCompletionAmount",this.getAttemptCompletionAmount());
					this.previousAttemptInformation.set("_attemptCompletionStatus",this.getAttemptCompletionStatus());
				}
			},
			
			newAttempt: function(){
				if (this.tracked == true) {
					// Attempt Progress Information 4.2.1.4
					this._attemptProgressStatus = false;
					this._attemptCompletionAmount = 0.0;
					this._attemptCompletionStatus = false;
					this.attemptAbsoluteDuration = 0.0;
					this.attemptExperiencedDuration = 0.0;
				}else{
					// Attempt Progress Information 4.2.1.4
					this._attemptProgressStatus = "unknown";
					this._attemptCompletionAmount = "unknown";//
					this._attemptCompletionStatus = "unknown";//
					this.attemptAbsoluteDuration = "unknown";
					this.attemptExperiencedDuration = "unknown";
				}
				this.objectives.each(function(id){
					this.at.objectives[id].newAttempt();
				},this)
			},

			initializeObj : function() {
				if (this.node.options.data.item.sequencing
						&& this.node.options.data.item.sequencing.objectives) {
					this.node.options.data.item.sequencing.objectives.objective.each(function(obj) {
						var o = new Objective(obj,this.node, this.at);
						this.at.objectives.set(obj.objectiveID, o);
						this.objectives.push(obj.objectiveID);
						obj.mapInfo.each(function(mi){
							this.at.globalObjectives.combine([mi.targetObjectiveID]);
						},this);
					}, this);
					var camObj = this.node.options.data.item.sequencing.objectives.primaryObjective;  
					var p = new Objective(camObj, this.node, this.at);
					p.setPrimary();
					this.at.objectives.set(camObj.objectiveID, p);
					this.primaryObjective = p;
					this.objectives.push(camObj.objectiveID);
					
					camObj.mapInfo.each(function(mi){
						this.at.globalObjectives.combine([mi.targetObjectiveID]);
					},this);
					
				}else{
					/*No objective defined for the activity, create artificial objective for tracking purpose*/
					this.primaryObjective = new Objective({objectiveID:"artificialObjective-"+this.node.options.data.item.identifier,mapInfo: new Hash()},this.node,node.at);
					this.primaryObjective.setPrimary();
					this.at.objectives.set(this.primaryObjective.id,this.primaryObjective);
					this.objectives.push(this.primaryObjective.id);
				}
			},

			setActivityIsSuspended : function(v) {
				this._activityIsSuspended = v;
				if (v == true) {
					this.node.domObj.getElement('a').grab(
							this.node.options.data.pause);
				} else {
					this.node.options.data.pause.dispose();
				}
			},
			getActivityIsSuspended : function() {
				return this._activityIsSuspended;
			},

			// Attempt Progress Information some getters and setters
			setAttemptCompletionStatus : function(v) {
				this._attemptCompletionStatus = v;
			},

			getAttemptCompletionStatus : function() {
				var v;
				if (this.primaryObjective.camObj.mapInfo.some(function(mi) {
						if (mi.readCompletionStatus == true
								&& this.at.globalObjectives[mi.targetObjectiveID]._attemptProgressStatus == true) {
							v = this.at.globalObjectives[mi.targetObjectiveID]._attemptCompletionStatus;
							return true;
						} else
							return false;
					}, this)) {
					return v;
				} else {
					return this._attemptCompletionStatus;
				}
			},

			setAttemptCompletionAmount : function(v) {
				this._attemptCompletionAmount = parseFloat(v);
			},

			getAttemptCompletionAmount : function() {
				var v;
				if (this.primaryObjective.camObj.mapInfo.some(function(mi) {
						if (mi.readProgressMeasure == true
								&& this.at.globalObjectives[mi.targetObjectiveID]._attemptCompletionAmount != 0) {
							v = this.at.globalObjectives[mi.targetObjectiveID]._attemptCompletionAmount;
							return true;
						} else
							return false;
					}, this)) {
					return v;
				} else {
					return this._attemptCompletionAmount;
				}
			},
		});


// Activity Progress Information getters and setters
// setActivityProgressStatus: function(v){
// // alert("setActivityProgressStatus "+v);
// this._activityProgressStatus = v;
// },
//	
// getActivityProgressStatus: function(){
// // alert("getActivityProgressStatus");
// return this._activityProgressStatus;
// },
//	
// setActivityAbsoluteDuration: function(v){
// this._activityAbsoluteDuration = v;
// },
//	
// getActivityAbsoluteDuration: function(){
// return this._activityAbsoluteDuration;
// },
//	
// setActivityExperiencedDuration: function(v){
// this._activityExperiencedDuration = v;
// },
//	
// getActivityExperiencedDuration: function(){
// return this._activityExperiencedDuration;
// },
//	
// setActivityAttemptCount: function(v){
// this._activityAttemptCount = v;
// },
//	
// getActivityAttemptCount: function(){
// return this._activityAttemptCount;
// },


/*
 * l'utilizzo di getter e setter permette la propagazione delle
 * informazioni come definito negli elemeti mapInfo del Content
 * Agregation Model
 */
// if (!Browser.Engine.trident){
// this.__defineSetter__("activityProgressStatus",
// this.setActivityProgressStatus);
// this.__defineGetter__("activityProgressStatus",
// this.getActivityProgressStatus);
// this.__defineSetter__("attemptProgressStatus",
// this.setAttemptProgressStatus);
// this.__defineGetter__("attemptProgressStatus",
// this.getAttemptProgressStatus);
//		    
// this.__defineSetter__("activityAbsoluteDuration",
// this.setActivityAbsoluteDuration);
// this.__defineGetter__("activityAbsoluteDuration",
// this.getActivityAbsoluteDuration);
//		    
// this.__defineSetter__("activityExperiencedDuration",
// this.setActivityExperiencedDuration);
// this.__defineGetter__("activityExperiencedDuration",
// this.getActivityExperiencedDuration);
//		    
// this.__defineSetter__("activityAttemptCount",
// this.setActivityAttemptCount);
// this.__defineGetter__("activityAttemptCount",
// this.getActivityAttemptCount);
//		    
//		    
// this.__defineSetter__("attemptCompletionStatus",
// this.setAttemptCompletionStatus);
// this.__defineGetter__("attemptCompletionStatus",
// this.getAttemptCompletionStatus);
//		    
// this.__defineSetter__("attemptCompletionAmount",
// this.setAttemptCompletionAmount);
// this.__defineGetter__("attemptCompletionAmount",
// this.getAttemptCompletionAmount);
//		    
// this.__defineGetter__("activityIsSuspended",
// this.getActivityIsSuspended);
// this.__defineSetter__("activityIsSuspended",
// this.setActivityIsSuspended);
// }