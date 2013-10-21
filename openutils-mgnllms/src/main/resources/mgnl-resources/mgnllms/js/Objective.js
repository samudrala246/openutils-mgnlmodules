/* Classe che rappresenta un obiettivo all'interno del Tracking Model */

// se vengono cambiati i nomi delle variabili che persistono i
// dati nel jcr, cambiare i corrispondenti campi in
// MagnoliaLearnerActivitiesManager.java

//PATCH aggiunto ai setter degli attribuiti degli obiettivi la propagazione utilizzando le mapInfo 

var Objective = new Class(
		{
			initialize : function(objective, node, at) {
				this.id = objective.objectiveID;
				this.camObj = objective;
				this.node = node;
				this.at = at;
				this.objectiveSatisfiedByMeasure = objective.objectiveSatisfiedByMeasure?objective.objectiveSatisfiedByMeasure:false;
			
				this.primaryObj = false;
				this.globalObj = false;

				/*
				 * valori nel caso l'attività a cui fanno riferimento gli
				 * obiettivi non sia tracciata (tracked = false)
				 */
				this._objectiveProgressStatus = "unknow";
				this._objectiveSatisfiedStatus = "unknow";
				this._objectiveMeasureStatus = "unknow";
				this._objectiveNormalizedMeasure = "unknow";

				/* valori di default */
				if (this.node && this.node.options.data.item.sequencing.deliveryControls.tracked) {
					this._objectiveProgressStatus = false;
					this._objectiveSatisfiedStatus = false;
					this._objectiveMeasureStatus = false;
					this._objectiveNormalizedMeasure = 0.0;
				}

				/*
				 * questi getter e setter permettono di utlizzare le
				 * informazioni presenti all'interno di mapInfo nel manifest che
				 * trasferiscono le informazioni tra obiettivi locali e globali
				 * non supportati da IE (anche 8)
				 */
				// this.__defineSetter__("objectiveProgressStatus",
				// this.setObjectiveProgressStatus);
				// this.__defineGetter__("objectiveProgressStatus",
				// this.getObjectiveProgressStatus);
				//
				// this.__defineSetter__("objectiveSatisfiedStatus",
				// this.setObjectiveSatisfiedStatus);
				// this.__defineGetter__("objectiveSatisfiedStatus",
				// this.getObjectiveSatisfiedStatus);
				//
				// this.__defineSetter__("objectiveMeasureStatus",
				// this.setObjectiveMeasureStatus);
				// this.__defineGetter__("objectiveMeasureStatus",
				// this.getObjectiveMeasureStatus);
				//
				// this.__defineSetter__("objectiveNormalizedMeasure",
				// this.setObjectiveNormalizedMeasure);
				// this.__defineGetter__("objectiveNormalizedMeasure",
				// this.getObjectiveNormalizedMeasure);
				
				/* funzioni per gestire la persistenza degli obiettivi */
				this.toJSON = _toJSON(/^(_current|at|node|id|camObj|objectiveSatisfiedByMeasure|caller)$/,
						this);
				this.fromJSON = _fromJSON.bind(this);
			},

			setPrimary : function() {
				this.primaryObj = true;
			},
			
			setGlobal: function() {
				this.globalObj = true;
			},
			
			newAttempt: function(){
				if(!this.globalObj){
					this._objectiveProgressStatus = false;
					this._objectiveSatisfiedStatus = false;
					this._objectiveMeasureStatus = false;
					this._objectiveNormalizedMeasure = 0.0;
				}
			},

			/*
			 * aggiorna le informazioni del Tracking Model
			 * basandosi dulle informazioni per lo stesso obiettivo presente nel
			 * data model
			 */
			update : function(cmiObj, at) {
				if (this.node && this.node.options.data.track.tracked){
					this._objectiveProgressStatus = (cmiObj.success_status != "unknown");
					this._objectiveSatisfiedStatus = (cmiObj.success_status == "passed");
	
					this._objectiveMeasureStatus = cmiObj.score && cmiObj.score.scaled != null;;
					this._objectiveNormalizedMeasure 
						= cmiObj.score && cmiObj.score.scaled != null ? parseFloat(cmiObj.score.scaled) :0;
					
					if (this.primaryObj) {
						// cmi.completion_status
						this.node.options.data.track._attemptProgressStatus = cmiObj.completion_status != "unknown";
						this.node.options.data.track._attemptCompletionStatus = cmiObj.completion_status == "passed";
						
						// cmi.progress_measure
						this.node.options.data.track._attemptCompletionAmount 
							= cmiObj.progress_measure != null ? parseFloat(cmiObj.progress_measure) : "unknown";
					}
					
					this.writeObjectiveMaps();
				}
			},
			
			/*
			 * controlla se si devono passare le informazioni modificate per
			 * questo obiettivo agli obiettivi globali eventualmente
			 * definiti nelle mapInfo
			 */
			writeObjectiveMaps: function(){
				this.camObj.mapInfo.some(
					function(mi) {
						if (mi.writeNormalizedMeasure == true) {
							this.at.globalObjectives[mi.targetObjectiveID]._objectiveNormalizedMeasure = this._objectiveNormalizedMeasure;
							this.at.globalObjectives[mi.targetObjectiveID]._objectiveMeasureStatus = this._objectiveMeasureStatus;
							return true;
						} else
							return false;
					}, this);
				$empty();

				this.camObj.mapInfo.some(
					function(mi) {
						if (mi.writeSatisfiedStatus == true) {
							this.at.globalObjectives[mi.targetObjectiveID]._objectiveProgressStatus = this._objectiveProgressStatus;
							this.at.globalObjectives[mi.targetObjectiveID]._objectiveSatisfiedStatus = this._objectiveSatisfiedStatus;
							return true;
						} else
							return false;
					}, this);

				this.camObj.mapInfo.some(
					function(mi) {
						if (mi.writeCompletionStatus == true) {
							this.at.globalObjectives[mi.targetObjectiveID]._attemptProgressStatus = this.node.options.data.track._attemptProgressStatus;
							this.at.globalObjectives[mi.targetObjectiveID]._attemptCompletionStatus = this.node.options.data.track._attemptCompletionStatus;
							return true;
						} else {
							return false;
						}
					}, this);

				this.camObj.mapInfo.some(
					function(mi) {
						if (mi.writeProgressMeasure == true) {
							this.at.globalObjectives[mi.targetObjectiveID]._attemptCompletionAmount = this.node.options.data.track._attemptCompletionAmount;
							return true;
						} else {
							return false;
						}
					}, this);
			},

			getObjectiveProgressStatus : function() {
				var v;
				if (this.camObj.mapInfo
						.some(
								function(mi) {
									if (mi.readSatisfiedStatus == true
											&& this.at.globalObjectives[mi.targetObjectiveID]._objectiveProgressStatus == true) {
										v = this.at.globalObjectives[mi.targetObjectiveID]._objectiveProgressStatus;
										return true;
									} else
										return false;
								}, this)) {
					return v;
				} else {
					return this._objectiveSatisfiedStatus;
				}
			},

			getObjectiveNormalizedMeasure : function() {
				for ( var mi in this.camObj.mapInfo) {
					var m = this.camObj.mapInfo[mi]
					if (m.readNormalizedMeasure
							&& this.at.globalObjectives[m.targetObjectiveID]._objectiveMeasureStatus == true) {
						return this.at.globalObjectives[m.targetObjectiveID]._objectiveNormalizedMeasure;
					}
				}
				return this._objectiveNormalizedMeasure;
			},

			getObjectiveSatisfiedStatus : function() {
				var v;
				if (this.camObj.mapInfo
						.some(
								function(mi) {
									if (mi.readSatisfiedStatus == true
											&& this.at.globalObjectives[mi.targetObjectiveID]._objectiveProgressStatus == true) {
										v = this.at.globalObjectives[mi.targetObjectiveID]._objectiveSatisfiedStatus;
										return true;
									} else
										return false;
								}, this)) {
					return v;
				} else {
					return this._objectiveSatisfiedStatus;
				}
			},

			getObjectiveMeasureStatus : function() {
				for ( var mi in this.camObj.mapInfo) {
					var m = this.camObj.mapInfo[mi]
					if (m.readNormalizedMeasure
							&& this.at.globalObjectives[m.targetObjectiveID]._objectiveMeasureStatus == true) {
						return this.at.globalObjectives[m.targetObjectiveID]._objectiveMeasureStatus;
					}
				}
				return this._objectiveNormalizedMeasure;
			},

			setObjectiveProgressStatus : function(v) {
				this._objectiveProgressStatus = v
				this.writeObjectiveMaps()
			},

			setObjectiveNormalizedMeasure : function(v) {
				this._objectiveNormalizedMeasure = parseFloat(v);
                this.writeObjectiveMaps()
			},

			setObjectiveSatisfiedStatus : function(v) {
				this._objectiveSatisfiedStatus = v;
                this.writeObjectiveMaps()
			},

			setObjectiveMeasureStatus : function(v) {
				this._objectiveMeasureStatus = v
				this.writeObjectiveMaps()
			}

		});