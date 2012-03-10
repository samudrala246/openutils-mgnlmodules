var GlobalSharedObjective = new Class({
	initialize: function(id){
		this.id = id;
		this._objectiveMeasureStatus = false;
		this._objectiveNormalizedMeasure = 0.0;
		this._objectiveProgressStatus = false;
		this._objectiveSatisfiedStatus = false;
	
		this._attemptCompletionStatus = false;
		this._attemptCompletionAmount = 0.0;
		this._attemptProgressStatus = false;
		
		this.activityProgressStatus = false;
		this.activityAbsoluteDuration = 0.0;
		this.activityExperiencedDuration = 0.0;
		this.activityAttemptCount = 0;
		
		this.primaryObj = false;
		
		this.toJSON = _toJSON(/^(_current|id|caller)$/,
				this);
		this.fromJSON = _fromJSON.bind(this);
	},
});