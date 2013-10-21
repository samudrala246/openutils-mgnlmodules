/* Classe che contiene le funzioni richiete dal rollup Process
 * potrebbe essere trasformata in un hash contentente funzioni da gestire con .bind()
 * le funzioni corrispondono a quelle descritte nello pseudocodice del manuale Sn dello SCORM*/
var RollupProcess = new Class({
	initialize: function(node,at) {
		this.node = node;
		this.at = at;
	},
		
	/* Overall Rollup Process [RB.1.5] */
	overallRollupProcess: function(){
		/* 1 */
		var actPath = this.node.pathFromRoot().reverse();
		/* 3 */
		actPath.each(function (node){
			if (node.nodes){
				this.measureRollupProcess(node)
				this.completionMeasureRollupProcess(node);
			}
			this.objectiveRollupProcess(node);
			this.activityProgressRollupProcess(node);
		},this);
	},
	
	/* Measure Rollup Process [RB.1.1 a] */
	measureRollupProcess: function(node) {
		var totalWeighted = 0;
		var validData = false;
		var countedMeasure = 0;
		/* 5 */
		var targetObj = this.node.options.data.track.primaryObjective;
		/* 6 */
		if (targetObj != null){
			node.nodes.some(function(child){
				/* 6.1.1 */
				if (child.options.data.item.sequencing.deliveryControls.tracked == true){
					var rolledupObj = child.options.data.track.primaryObjective
					/* 6.1.1.3 */
					if (rolledupObj){
						countedMeasure += parseFloat(child.options.data.item.sequencing.rollupRules.objectiveMeasureWeight);
						if (rolledupObj.getObjectiveMeasureStatus() == true){
							totalWeighted += parseFloat(child.options.data.item.sequencing.rollupRules.objectiveMeasureWeight) *
								rolledupObj.getObjectiveNormalizedMeasure();
						}
						validData = true;
						return false; // continuo il ciclo node.nodes.some
					}else{
						return true; // esco da node.nodes.some
					}
				}
			},this);
			/* 6.2 */
			if (validData == false){
				targetObj.setObjectiveMeasureStatus(false);
			}else{
				if (countedMeasure > 0){
					targetObj.setObjectiveMeasureStatus(true);
					targetObj.setObjectiveNormalizedMeasure(totalWeighted / countedMeasure);
				}else{
					targetObj.setObjectiveMeasureStatus (false);
				}
			}
		}
	},
	
	/* Completion Measure Rollup Process [RB.1.1 b] */
	completionMeasureRollupProcess: function (node){
		var totalWeighted = 0;
		var validData = false;
		var countedMeasure = 0;
		/* 4 */
		node.nodes.each(function(child){
			if (child.options.data.item.sequencing.deliveryControls.tracked == true){
				countedMeasure += parseFloat(child.options.data.item.completionThreshold.progressWeight);
				if(child.options.data.track.getAttemptCompletionAmount()!=0){ 
					totalWeighted += child.options.data.track.getAttemptCompletionAmount() * 
						parseFloat(child.options.data.item.completionThreshold.progressWeight);
					validData = true;
				}
			}
		});
		/* 5 */
		if (validData == false){
			node.options.data.track.setAttemptCompletionAmount(0);
		}else{
			if (countedMeasure > 0){
				node.options.data.track.setAttemptCompletionAmount(totalWeighted / countedMeasure);
			}else{
				node.options.data.track.setAttemptCompletionAmount(0);
			}
		}
	},
	
	/* Objective Rollup Process[RB.1.2] */
	objectiveRollupProcess:  function(node){
		this.objectiveRollupMeasureProcess(node);
		this.objectiveRollupRulesProcess(node);
	},
	
	/* Objective Rollup Using Measure Process [RB.1.2 a] */
	objectiveRollupMeasureProcess: function(node){
		var target = node.options.data.track.primaryObjective;
		if (target){
			if (target.objectiveSatisfiedByMeasure == true){
				if (target.getObjectiveMeasureStatus() == false){
					target.setObjectiveProgressStatus(false);
				}else{
					if (node.options.data.track.activityIsActive == false
							||(node.options.data.track.activityIsActive == true
									&& node.options.data.item.sequencing.rollupConsiderations.measureSatisfactionIfActive == true)){
						if(target.getObjectiveNormalizedMeasure() >= parseFloat(target.camObj.minNormalizedMeasure)){
							target.setObjectiveProgressStatus(true);
							target.setObjectiveSatisfiedStatus(true);
						}else{
							target.setObjectiveProgressStatus(true);
							target.setObjectiveSatisfiedStatus(false);
						}
					}else{
						//target.setObjectiveProgressStatus(false);
					}
				}
			}
		}
	},
	
	/* Objective Rollup Using Rules Process [RB.1.2 b] */
	objectiveRollupRulesProcess: function(node){
		if (node.options.data.item.sequencing.rollupRules 
				&& !node.options.data.item.sequencing.rollupRules.rollupRule.map(function(rr){return rr.rollupAction.action.toLowerCase()}).contains('satisfied')
				&& !node.options.data.item.sequencing.rollupRules.rollupRule.map(function(rr){return rr.rollupAction.action.toLowerCase()}).contains('notsatisfied')){
			/*
			 * Apply a Rollup Rule to the activity with a Rollup Child Activity
			 * Set ofAll; a Rollup Condition of Satisfied; and a Rollup Action
			 * of Satisfied
			 * 
			 * Apply a Rollup Rule to the activity with a Rollup Child Activity
			 * Set ofAll; a Rollup Condition of Objective Status Known; and a
			 * Rollup Action of Not Satisfied
			 */
			
			var rr = new Hash({
				childActivitySet: "all",
				rollupConditions: new Hash({
					conditionCombination: 'any',
					rollupCondition: [{
						condition: 'satisfied',
						operator: 'noop'
					}]
				}),
				rollupAction: new Hash({
					action: 'satisfied'
				})
			});
			
			/*
			 * inietto le regole di default all'interno delle rolluprules gli if
			 * successivi applicano le regole di default
			 */
			node.options.data.item.sequencing.rollupRules.rollupRule.push(rr);
			var rr2 = new Hash(rr);
			rr2.rollupConditions.rollupCondition[0].condition = 'objectiveStatusKnown';
			rr2.rollupAction.action = 'notsatisfied';
			node.options.data.item.sequencing.rollupRules.rollupRule.push(rr2);
		}
		var target = node.options.data.track.primaryObjective;
		if (target){
			if(this.rollupRuleCheck(node, 'notsatisfied')){
				target.setObjectiveProgressStatus(true);
				target.setObjectiveSatisfiedStatus(false);
			}
			if(this.rollupRuleCheck(node, 'satisfied')){
				target.setObjectiveProgressStatus(true);
				target.setObjectiveSatisfiedStatus(true);
			}
		}
	},
	
	/* Activity Progress Rollup Process RB.1.3 */
	activityProgressRollupProcess: function(node) {
		this.activityRollupUsingMeasureProcess(node);
		this.activityRollupUsingRulesProcess(node);
	},
	
	/* Activity Progress Rollup Using Measure Process [RB.1.3 a] */
	activityRollupUsingMeasureProcess: function(node){
		var tm = node.options.data.track;
		if (node.options.data.item.completionThreshold.completedByMeasure == true){
			
			tm.setAttemptProgressStatus(false);
			tm.setAttemptCompletionStatus(false);
			
			if (tm.attemptCompletionAmount == 0){
				tm.setAttemptCompletionStatus(false);
			}else{
				if (tm.getAttemptCompletionAmount() >= parseFloat(node.options.data.item.completionThreshold.minProgressMeasure)){
					tm.setAttemptProgressStatus( true);
					tm.setAttemptCompletionStatus(true);
				}else{
					tm.setAttemptProgressStatus(true);
					tm.setAttemptCompletionStatus( false);
				}
			}
		}else{
			//tm.setAttemptProgressStatus(false);
		} 
	},
	
	/* Activity Progress Rollup Using Rules Process [RB.1.3 b] */
	activityRollupUsingRulesProcess: function(node){
		var tm = node.options.data.track;
		if (node.options.data.item.sequencing.rollupRules
				&& !node.options.data.item.sequencing.rollupRules.rollupRule.map(function(rr){return rr.rollupAction.action.toLowerCase()}).contains('incomplete')
				&& !node.options.data.item.sequencing.rollupRules.rollupRule.map(function(rr){return rr.rollupAction.action.toLowerCase()}).contains('completed')){
			/*
			 * Apply a Rollup Rule to the activity with a Rollup Child Activity
			 * Set of All; a Rollup Condition of Completed; and a Rollup Action
			 * of Completed
			 * 
			 * Apply a Rollup Rule to the activity with a Rollup Child Activity
			 * Set of All;a Rollup Condition of Activity Progress Known; and a
			 * Rollup Action of Incomplete
			 */
			var rr = new Hash({
				childActivitySet: "all",
				rollupConditions: new Hash({
					conditionCombination: 'any',
					rollupCondition: [{
						condition: 'completed',
						operator: 'noop'
					}]
				}),
				rollupAction: new Hash({
					action: 'completed'
				})
			});
			
			/*
			 * inietto le regole di default all'interno delle rolluprules gli if
			 * successivi applicano le regole di default
			 */
			node.options.data.item.sequencing.rollupRules.rollupRule.push(rr);
			var rr2 =new Hash(rr);
			rr2.rollupConditions.rollupCondition[0].condition = 'activityProgressKnown';
			rr2.rollupAction.action = 'incomplete';
			node.options.data.item.sequencing.rollupRules.rollupRule.push(rr2);
			
		}
		if (this.rollupRuleCheck(node,'incomplete')== true){
			tm.setAttemptProgressStatus(true);
			tm.setAttemptCompletionStatus(false);
		}
		
		if (this.rollupRuleCheck(node,'completed')== true){
			tm.setAttemptProgressStatus(true);
			tm.setAttemptCompletionStatus(true);
		}
	},
	
	/* Check Child for Rollup Subprocess [RB.1.4.2] */
	checkChildForRollup: function(child, rr) {
		var included = false;
		var track = child.options.data.track;
		if (rr.rollupAction.action.toLowerCase() == 'satisfied'
				|| rr.rollupAction.action.toLowerCase() == 'notsatisfied')
			if (child.options.data.item.sequencing.rollupRules.rollupObjectiveSatisfied==true){
				included = true;
				if ((rr.rollupAction.action== 'satisfied' 
						&& child.options.data.item.sequencing.rollupConsiderations.requiredForSatisfied.toLowerCase() =="ifnotsuspended") || 
						(rr.rollupAction.action== 'notSatisfied' 
						&& child.options.data.item.sequencing.rollupConsiderations.requiredForNotSatisfied.toLowerCase() =="ifnotsuspended")) {
					if (track.activityProgressStatus == false
							|| (track.activityAttemptCount > 0 && track.getActivityIsSuspended()==true))
						included = false;
				}
				else{
					if ((rr.rollupAction.action== 'satisfied'
							&& child.options.data.item.sequencing.rollupConsiderations.requiredForSatisfied.toLowerCase() =="ifattempted") || 
							(rr.rollupAction.action== 'notSatisfied'
							&& child.options.data.item.sequencing.rollupConsiderations.requiredForNotSatisfied.toLowerCase() =="ifattempted")) {
						if (track.activityProgressStatus == false || track.activityAttemptCount == 0 )
							included = false;
					}
					else{
						if ((rr.rollupAction.action== 'satisfied'
								&& child.options.data.item.sequencing.rollupConsiderations.requiredForSatisfied.toLowerCase() =="ifnotskipped") || 
								(rr.rollupAction.action== 'notSatisfied'
								&& child.options.data.item.sequencing.rollupConsiderations.requiredForNotSatisfied.toLowerCase() =="ifnotskipped")) {
							/*
							 * Apply the Sequencing Rules Check Process to the activity and its Skip
							 * sequencing rules
							 */
							if(this.sequencingRulesCheckProcess(node,[rr.rollupAction.action])!=null)
								included = false;
						}
					}
				}
			}
		if (rr.rollupAction.action.toLowerCase() == 'completed'
				|| rr.rollupAction.action.toLowerCase() == 'incomplete'){
			if(child.options.data.item.sequencing.rollupRules.rollupProgressCompletion == true)
				included = true;
			if ((rr.rollupAction.action.toLowerCase() == 'completed'
					&& child.options.data.item.sequencing.rollupConsiderations.requiredForCompleted.toLowerCase()=="ifnotsuspended") || 
					(rr.rollupAction.action.toLowerCase() == 'incomplete' 
					&& child.options.data.item.sequencing.rollupConsiderations.requiredForIncompleted.toLowerCase()=="ifnotsuspended")) {	
				if (track.activityProgressStatus == false 
						|| (track.activityAttemptCount > 0 && track.getActivitiIsSuspended()==true))
					included = false;
			}
			else{
				if ((rr.rollupAction.action.toLowerCase() == 'completed'
						&& child.options.data.item.sequencing.rollupConsiderations.requiredForCompleted.toLowerCase() =="ifattempted") || 
						(rr.rollupAction.action.toLowerCase() == 'incomplete'
						&& child.options.data.item.sequencing.rollupConsiderations.requiredForIncompleted.toLowerCase() =="ifattempted")) {
					if (!track.activityProgressStatus || track.activityAttemptCount == 0 )
						included = false;
				}
				else{
					if ((rr.rollupAction.action.toLowerCase() == 'completed'
							&& child.options.data.item.sequencing.rollupConsiderations.requiredForCompleted.toLowerCase() =="ifnotskipped") || 
							(rr.rollupAction.action.toLowerCase()== 'incomplete'
							&& child.options.data.item.sequencing.rollupConsiderations.requiredForIncompleted.toLowerCase() =="ifnotskipped")) {
						if(sequencingRulesCheckProcess(node,[rr.rollupAction.action])!=null)
							included = false;
					}
				}
			}
		}
		return included;
	},
	
	/* Sequencing Rule Check Subprocess */ 
	sequencingRuleCheckProcess: function(node,sr) {
		var bag = sr.ruleConditions.ruleCondition.map(function(rc){
			if(!rc.referencedObjective && node.options.data.item.sequencing.objectives){
				rc.referencedObjective = node.options.data.item.sequencing.objectives.primaryObjective.objectiveID;
			}
			var sc = new seqCondition(node,this.at);
			var r = sc[rc.condition.toUpperCase()](rc)
			return r=="unknown" ? "unknown": (rc.operator.toLowerCase()=="not") != r;
		},this);
		return bag[sr.ruleConditions.conditionCombination.toLowerCase()](function(v){
			return v==true;
		})
	},
	
	/* Sequencing Rules Check Process [UP.2] */
	sequencingRulesCheckProcess: function(node,actions) {
		/*
		 * If the activity includes Sequencing Rules with any of the specified
		 * Rule Actions Then
		 */
		var act=null;
		var sr = node.options.data.item.sequencing && node.options.data.item.sequencing.sequencingRules ? node.options.data.item.sequencing.sequencingRules: false;
		
		function filterRules(rr){
			return (actions.contains(rr.ruleAction.action.toLowerCase())) && rr.ruleConditions.ruleCondition.every(function(rc){
				/*
				 * elimina (rimuove dalla valutazione) le regole il cui referencedObjectives non è definito
				 * all'interno dell'attività corrente
				 */
				return (!rc.referencedObjective || this.at.objectives[rc.referencedObjective].node == node);
			},this);
		};
		
		if(sr){
			sr.exitConditionRule.filter(filterRules,this).concat(
			sr.preConditionRule.filter(filterRules,this).concat(
			sr.postConditionRule.filter(filterRules,this))).some(function(rr) {
				if (this.sequencingRuleCheckProcess(node,rr) == true){ 
					act=rr.ruleAction.action.toLowerCase()
					return true;
				}
				return false;
			},this);
		}
		return act;
	},
	
	/* Rollup Rule Check Subprocess [RB.1.4] SN192 */
	rollupRuleCheck: function(node, action) {
		return !!node.options.data.item.sequencing.rollupRules
			&& node.options.data.item.sequencing.rollupRules.rollupRule.filter(function(rr) {
			return (rr.rollupAction.action.toLowerCase()==action);
		}).some(function(rr) {
			var contributingChildren = new Array();
			if (node.nodes){
				node.nodes.each(function(child) {
					if(child.options.data.track.tracked){
						if (this.checkChildForRollup(child, rr)==true){
							contributingChildren.push(this.evaluateRollupConditions(rr, child));
						}
					}
				},this);
			}
			
			var child = contributingChildren[rr.childActivitySet.toLowerCase()](
					function(i){
						return i && i!='unknown'
					},
					rr.minimumCount ? rr.minimumCount : rr.minimumPercent
				);

			// vecchia funzione
			// && contributingChildren[rr.childActivitySet.toLowerCase()](function(i){return i && i!='unknown'},rr.minimumCount ? rr.minimumCount : rr.minimumPercent);
			var check = !!contributingChildren.length &&  child ;
				
			return check;
		},this);
	
	},
	
	/* Evaluate Rollup Conditions Subprocess [RB.1.4.1] */
	evaluateRollupConditions: function(rr, node) {
		return rr.rollupConditions.rollupCondition.map(function(ruCond) {
			var sc = new seqCondition(node,this.at);
			var res = sc[ruCond.condition.toUpperCase()]({referencedObjective:""});
			return res=="unknown"?"unknown":(ruCond.operator.toLowerCase()=="not") != res;
		},this)[rr.rollupConditions.conditionCombination.toLowerCase()](function(item){return item==true});
	}
});