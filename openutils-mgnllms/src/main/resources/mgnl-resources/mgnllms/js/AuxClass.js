Array.implement({
	count: function(fn){
		var c = 0;
		this.each(function(item) {
			if (fn(item)) c++;
		});
		return c;
	},
	atLeastCount: function(fn, n) {
		return this.count(function(i){
			return i && i!='unknown';
		})>=n;
	},
	atLeastPercent: function(fn, n) {
		return (this.count(function(i){
			return i && i != 'unknown'
		},n)/this.length)>=n;
	},
	none: function(fn,bind) {
		return !this.every(function(i){return i && i != 'unknown'},bind)
	},
	all: function(fn,bind) {
		return this.every(fn,bind)
	},
	any: function(fn,bind) {
		return this.some(fn,bind)
	},
	shuffle: function() {
		// destination array
		// http://davidwalsh.name/array-shuffling-mootools
		for(var j, x, i = this.length; i; j = parseInt(Math.random() * i), x = this[--i], this[i] = this[j], this[j] = x);
		return this;
	}

});

var ScormTime = new Class({
	timeIntervalRE : /^P(\d+Y)?(\d+M)?(\d+D)?(T(\d+H)?(\d+M)?(\d+(\.\d\d?)?S)?)?$/,
	indexes : new Hash([365*24*60*60, 		// anni
	                        31*24*60*60,  	// mesi
	                        24*60*60,		// giorni
	                        60*60,			// ore
	                        60,				// minuti
	                        1				// secondi
	                        ].associate([1,2,3,5,6,7])),
	second: 0,
	
	initialize: function(time){
		if ($type(time)=="string"){
			this.time=time;
			var timeArray = time.match(this.timeIntervalRE);
			this.indexes.each(function(v,k){
				 /*
					 * parseFloat() considera soli il primo numero all'interno
					 * della stringa, non è necessario eliminare gli
					 * identificatori Y,M, etc
					 */
				this.second += parseFloat(timeArray[k]?timeArray[k]:0)*v;
			},this);
		}else if ($type(time)=="number"){
			this.second = time;
			
			var a = String(this.second).split(".").map(function(i){return parseInt(i)});
			this.auxSecond = a[0];
			
			var b =false;
			var append  = ["Y","M","D","H","M","S"];
			
			var timeArray = this.indexes.map(function(v){
				var toReturn = Math.floor(this.auxSecond / v);
				this.auxSecond -= toReturn*v;
				return toReturn;
			},this);
			this.time = "P"+timeArray.getValues().map(function(v,i){
				var temp = v? (i==5 && a[1] ?v+"."+a[1]: v) + append[i]: "";
				if (!b && v && i>=3){
					temp = "T" + temp;
					b=true;
				}
				return temp;
			}).toString().replace(/,/g,"");
		}else{
			$empty();
		}
	},
	
	add: function(t){
		return new ScormTime(t.second+this.second);
	},
	
	toString: function(){
		return this.time;
	}
});

function scormAND(a,b){
	if (a==false||b==false){
		return false;
	}
	if (a=="unknown" || b=="unknown"){
		return "unknown";
	}
	if (a==true && b==true){
		return true;
	}
}

/*
 * funzione che filtra le proprieta che corrispondono all'espressione regolare
 * filter e ritorna un hash senza queste proprietà per poterlo serializzare
 */
function _toJSON(filter,bind){
	return function(){
		var h = new Hash();
		$each(bind,function(a,b,hash){
			if (!filter.test(b)){
				h.set(b,hash[b]);
			}
		});
		return h;
	};
}
function _fromJSON(obj){
	obj.each(function(value,key){
		this[key] = value;
	},this)
}

/*
 * Classe i cui metodi sono nomi di condizioni di sequencing e ritornano
 * true/false/"unknown" a seconda se sono soddisfatte le condizioni [SN]Table
 * 3.4.2a: Description of Sequencing Rule Conditions per le condizioni che fanno
 * riferimento ad obiettivi,
 */
var seqCondition = new Class({
	
	initialize: function(node,at){
		this.node = node;
		this.tm = node.options.data.track
		this.at = at;
	},
	
	getObjective: function(refObj){
		if(refObj && this.at.objectives.has(refObj) && this.at.objectives[refObj].node == this.node){
			return this.at.objectives[refObj];
		}else if(refObj=="") {
			return this.node.options.data.track.primaryObjective;
		}else{
			return null;
		}
	},
	
	SATISFIED: function(rc) {
		var obj = this.getObjective(rc.referencedObjective);
		if (obj){
			return scormAND(obj.getObjectiveProgressStatus(), obj.getObjectiveSatisfiedStatus());
		}else {
			return "unknown";
		}
	},
	OBJECTIVESTATUSKNOWN: function(rc) {
		var obj = this.getObjective(rc.referencedObjective);
		if (obj){
			return obj.getObjectiveProgressStatus();
		}else {
			return "unknown";
		}
	},
	
	
	
	OBJECTIVEMEASUREKNOWN: function(rc) {
		var obj = this.getObjective(rc.referencedObjective);
		if (obj){
			return obj.getObjectiveMeasureStatus();
		}else {
			return "unknown";
		}
	},
	
	OBJECTIVEMEASUREGREATERTHAN: function(rc) {
		var obj = this.getObjective(rc.referencedObjective);
		if (obj){
			return scormAnd(obj.getObjectiveMeasureStatus(),
					obj.getObjectiveNormalizedMeasure()>parseFloat(rc.measureThreshold));
		}
		else{
			return "unknown";
		}
	},
	
	
	OBJECTIVEMEASURELESSTHAN: function(rc) {
		var obj = this.getObjective(rc.referencedObjective);
		if (obj){
			return scormAnd(obj.getObjectiveMeasureStatus(),
					obj.getObjectiveNormalizedMeasure()<parseFloat(rc.measureThreshold));
		}
		else{
			return "unknown";
		}
	},
	
	
	COMPLETED: function(rc) {
		var res =scormAND(this.tm.getAttemptProgressStatus(),this.tm.getAttemptCompletionStatus());
		//var res =this.tm.getAttemptCompletionStatus();
		return res;
	},
	ACTIVITYPROGRESSKNOWN: function(rc) {
		return scormAND(this.tm.activityProgressStatus,this.tm.getAttemptProgressStatus());
	},
	ALWAYS: function(rc) {
		return true;
	},
	ATTEMPTED: function(rc){
		return parseInt(this.node.options.data.track.activityAttemptCount) > 0;
	},
	ATTEMPTLIMITEXCEEDED: function(rc) {
		return scormAND(scormAND(
				this.tm.activityProgressStatus,
				!!this.node.options.data.item.sequencing.limitConditions.attemptLimit ),
				parseInt(this.node.options.data.track.activityAttemptCount) 
					>= parseFloat(this.node.options.data.item.sequencing.limitConditions.attemptLimit));
	},
	OBJECTIVE_MEASURE_LESS_THAN: function (rc){
		return this.OBJECTIVEMEASURELESSTHAN(rc)
	},
	OBJECTIVE_MEASURE_GREATER_THAN:function (rc){ 
		return this.OBJECTIVEMEASUREGREATERTHAN(rc)
	},
	OBJECTIVE_MEASURE_KNOWN: function (rc){
		return this.OBJECTIVEMEASUREKNOWN(rc)
	},
	OBJECTIVE_STATUS_KNOWN: function (rc){
		return this.OBJECTIVESTATUSKNOWN(rc)
	},
	ATTEMPT_LIMIT_EXCEEDED: function(rc){
		return this.ATTEMPTLIMITEXCEEDED(rc);
	},
	ACTIVITY_PROGRESS_KNOWN: function(rc){
		return this.ACTIVITYPROGRESSKNOWN(rc);
	}
});

/*
 * Hash che contiene solo funzioni, corrisponde allo switch case presente nello
 * pseudocodice per il Navigation Request Process
 */ 
/* Navigation Request Process [NB.2.1] */
var navigationRequest = new Hash({
	/* case 1 */
	start: function (){
		if (!this.current){
			return {
				valid: true,
				terminationRequest: null,
				sequencingRequest: 	'start',
				targetActivity: 	null,
				exception: null
			};
		}else{
			return {
				valid: false,
				terminationRequest: null,
				sequencingRequest: 	null,
				targetActivity: 	null,
				exception: "NB.2.1-1"
			};
		}
	},
	/* case 2 */
	resumeall: function(){
		var temp = {
			valid: false,
			terminationRequest: null,
			sequencingRequest: 	null,
			targetActivity: 	null,
			exception: null
		};
		if (!this.current){
			/* If the Suspended Activity is Defined Then */
			/*
			 * Make sure the previous sequencing session ended with a suspend
			 * all request.
			 */
			if(this.suspendedActivity != null){
				temp.valid=true;
				temp.sequencingRequest = 'resumeAll';
				return temp;
			}else{
				temp.exception = "NB.2.1-3"
				return temp;
			}

		}else{
			temp.exception="NB.2.1-1";
			return temp;
		}
	},
	
	/* case 3 */
	'continue': function(){
		var temp = {
			valid: false,
			terminationRequest: null,
			sequencingRequest: 	null,
			targetActivity: 	null,
			exception: null
		};
		if (!this.current){
			temp.exception = "NB.2.1-2";
			return temp;
		}
		if (this.current != this.root
				&& this.current.owner.getControlMode().flow== true){
			if (this.current.options.data.track.activityIsActive ==  true){
				temp.valid = true;
				temp.terminationRequest = 'exit';
				temp.sequencingRequest = 'continue';
				return temp
			}else{
				temp.valid = true;
				temp.sequencingRequest = 'continue';
				return temp;
			}
		}else{
			temp.exception = "SB.2.1-4";
			return temp;
		}
	},
	/* case 4 */
	previous: function(){
		var temp = {
			valid: false,
			terminationRequest: null,
			sequencingRequest: 	null,
			targetActivity: 	null,
			exception: null
		};
		if (!this.current){
			temp.exception = "NB.2.1-2";
			return temp;
		}
		if (this.current != this.root){
			if (this.current.owner.getControlMode().flow == true
					&& this.current.owner.getControlMode().forwardOnly == false){
				if (this.current.options.data.track.activityIsActive == true){
					temp.valid = true;
					temp.terminationRequest = 'exit';
					temp.sequencingRequest = 'previous';
					return temp;
				}else{
					temp.valid = true;
					temp.sequencingRequest = 'previous';
					return temp;
				}
			}else{
				temp.exception = 'NB.2.1-5';
				return temp;
			}
		}else{
			temp.exception = 'NB.2.1-6';
			return temp;
		}
	},
	
	/* Behavior not defined. case 5 */
	forward: function (){
		return {
			valid: false,
			terminationRequest: null,
			sequencingRequest: 	null,
			targetActivity: 	null,
			exception: "NB.2.1-7"
		};
	},
	
	/* Behavior not defined. case 6 */
	backward: function (){
		return {
			valid: false,
			terminationRequest: null,
			sequencingRequest: 	null,
			targetActivity: 	null,
			exception: "NB.2.1-7"
		};
	},
	
	/* case 7 */
	choice: function(target){
		var temp = {
			valid: false,
			terminationRequest: null,
			sequencingRequest: 	null,
			targetActivity: 	null,
			exception: null
		};
		var node
		if (target.getControlMode){
			node = target;
		}else{
			// trovare nodo a cui corrisponde target
			node = this.preorderFull.filter(function(n){
				if (n.getName() == target){
					return true;
				}else return false;
			})[0];
		}
		/* 7.1 */
		if (node && this.root.findChild([node.options.data.item.identifier])||this.root==node){
			/* 7.1.1 */
			if (this.root==node
					|| (node.owner.getControlMode().choice == true)){
				if (!this.current){
					temp.valid = true;
					temp.sequencingRequest = 'choice';
					temp.target = node;
					return temp;
				}
				if (this.current.owner!=node.owner){
					var nodePath = node.pathFromRoot().reverse();
				
					var curPath = this.current.pathFromRoot().reverse();
				
					var ancestor = curPath.pop();
					nodePath.pop();
					while(curPath.length && nodePath.length 
							&& curPath.getLast()==nodePath.getLast()){
						ancestor = curPath.pop();
						nodePath.pop();
					}
					
					if (curPath.length > 0){
						if (curPath.some(function(n){
							if (n.options.data.track.activityIsActive == true
									&& n.getControlMode().choiceExit == false){
								return true;
							}
							return false;
						})){
							temp.exception = "NB.2.1-8";
							return temp;
						}
					}else{
						temp.exception = "NB.2.1-9";
						return temp;
					}
				}
				/* 7.1.1.3 */
				if (this.current.options.data.track.activityIsActive == false
						&& this.current.getControlMode().choiceExit == false){
					temp.exception = 'NB.2.1-8';
					return temp;
				}
				/* 7.1.1.4 */
				if (this.current.options.data.track.activityIsActive == true){
					temp.valid = true;
					temp.terminationRequest = 'exit';
					temp.sequencingRequest = 'choice';
					temp.target = node;
					return temp;
				}else{
					temp.valid = true;
					temp.sequencingRequest = 'choice';
					temp.target = node;
					return temp;
				}
			}else{ /* 7.1.2 */
				temp.exception = 'NB.2.1-10';
				return temp;
			}
		}else{ /* 7.2 */
			temp.exception = 'NB.2.1-11';
			return temp;
		}
	},
	
	/* case 8 */
	exit: function(){
		var temp = {
			valid: false,
			terminationRequest: null,
			sequencingRequest: 	null,
			targetActivity: 	null,
			exception: null
		};
		if (this.current){
			if (this.current.options.data.track.activityIsActive){
				temp.valid = true;
				temp.terminationRequest = 'exit';
				temp.sequencingRequest = 'exit';
			}else{
				temp.exception = "NB.2.1-12";
			}
		}else{
			temp.exception = "NB.2.1-2";
		}
		return temp;
	},
	/* case 9 */
	exitall: function(){
		var temp = {
				valid: false,
				terminationRequest: null,
				sequencingRequest: 	null,
				targetActivity: 	null,
				exception: null
			};
		if (this.current){
			temp.valid = true;
			temp.terminationRequest= 'exitall';
			temp.sequencingRequest= 	'exit';
		}else{
			temp.exception = 'NB.2.1-2';
		}
		return temp;
	},
	/* case 10 */
	abandon: function(){
		var temp = {
				valid: false,
				terminationRequest: null,
				sequencingRequest: 	null,
				targetActivity: 	null,
				exception: null
			};
		if (this.current){
			if (this.current.options.data.track.activityIsActive == true){
				temp.valid = true;
				temp.terminationRequest= 'abandon';
				temp.sequencingRequest=	'exit';
				return temp;
			}else{
				temp.exception = 'NB.2.1-12';
				return temp;
			}
		}else{
			temp.exception = 'NB.2.1-2';
			return temp;
		}
	},
	/* case 11 */
	abandonall: function(){
		var temp = {
				valid: false,
				terminationRequest: null,
				sequencingRequest: 	null,
				targetActivity: 	null,
				exception: null
			};
		if (this.current){
			temp.valid = true;
			temp.terminationRequest= 'abandonall';
			temp.sequencingRequest=	'exit';
			return temp;
		}else{
			temp.exception = 'NB.2.1-2';
			return temp;
		}
	},
	/* case 12 */
	suspendall: function(){
		var temp = {
				valid: false,
				terminationRequest: null,
				sequencingRequest: 	null,
				targetActivity: 	null,
				exception: null
			};
		if (this.current){
			temp.valid = true;
			temp.terminationRequest= 'suspendall';
			temp.sequencingRequest=	'exit';
			return temp;
		}else{
			temp.exception = 'NB.2.1-2';
			return temp;
		}
	},
	/* case 13 */
	jump: function(target){
		var temp = {
			valid: false,
			terminationRequest: null,
			sequencingRequest: 	null,
			target: 	null,
			exception: null
		};
		var node
		if (target.getControlMode){
			node = target 
		}else{
			// trova il nodo a cui corrisponde target
			node = this.preorderFull.filter(function(n){
				if (n.getName() == target){
					return true;
				}else return false;
			})[0];
		}
		if (node && node.owner.availableChildren().contains(node)){
			temp.valid = true;
			temp.terminationRequest= 'exit';
			temp.sequencingRequest='jump';
			temp.target = node;
			return temp;
		}else{
			temp.exception = 'NB.2.1-2';
			return temp;
		}
	}
});