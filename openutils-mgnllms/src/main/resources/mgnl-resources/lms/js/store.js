ErrorMessages = new Hash( {
	0   : "No error", // controllare stringa esatta
	103 : "Already Initialized",
	104 : "Content Instance Terminated",
	112 : "Termination Before Initialization",
	113 : "Termination After Termination",
	122 : "Retrieve Data Before Initialization",
	123 : "Retrieve Data After Termination",
	132 : "Store Data Before Initialization",
	133 : "Store Data After Termination",
	142 : "Commit Before Initialization",
	143 : "Commit After Termination",
	201 : "General Argument Error",
	301 : "General Get Failure",
	351 : "General Set Failure",
	401 : "Undefined Data Model Element",
	403 : "Data Model Element Value Not Initialized",
	404 : "Data Model Element Is Read Only",
	405 : "Data Model Element Is Write Only",
	406 : "Data Model Element Type Mismatch",
	407 : "Data Model Element Value Out Of Range",
	408 : "Data Model Dependency Not Established",
	1000: "Module error",
});

NOT_INITIALIZED = 0;
RUNNING = 1;
TERMINATE = 2;

var ScormError = new Class( {
	Extends : Error,
	initialize : function(errorNumber, info) {
		this.errorNumber = errorNumber;
		this.message = this.errorNumber + " "
				+ ErrorMessages.get(this.errorNumber);
		if (info) {
			this.info = info;
			console.error(errorNumber,this.info)
		}
	}
});

var Store = new Class( {

	cmi : {
		comments_from_learner : $empty(),
		comments_from_lms : $empty(),
		interactions : $empty(),
		objectives : $empty(),
		score : $empty(),
		learner_preference: new Hash({
			_children : ['audio_level','language','delivery_speed','audio_captioning'],
			audio_level : 1,
			language: "",
			delivery_speed : 1,
			audio_captioning: 0,
		}),
		completion_status: "unknown",
		success_status: "unknown",
		completion_threshold: "",
		
	},
	adl : {
		nav: $empty(),
		data: $empty(),
	},

	initialize : function(at) {
		this.cmi.comments_from_learner = new CommentsFrom();
		this.cmi.comments_from_lms = new CommentsFrom();
		this.cmi.interactions = new Interactions();
		this.cmi.objectives = new Objectives();
		this.cmi.score = new Score();
		this.regexpIndexed = /\.\b(0|[1-9][0-9]*)\b/g;
		this.at = at;
		this.adl.nav = {};
		this.adl.nav["request"]="_none_";
		this.adl.nav.request_valid = new RequestValid(this.at);
		this.adl.data = new Data();
	},
	
	expandKey : function(key, isGetter) {
		var key = key;
		var workingKey = key;
		var test = workingKey.match(this.regexpIndexed);
		if (test) {
			test.each(function(item) {
				workingKey = workingKey.replace(item, ".get("
						+ item.substr(1, item.length - 1) + ", " + isGetter
						+ ")");
			});
		}else{
			var m = workingKey.match(/adl\.nav\.request_valid\.(.*)/);
			if (m){
				workingKey=workingKey.replace(m[1],"get(\""+m[1]+"\")");
			}
		}
		return workingKey;
	},

	get : function(cmiKey) {
		validatorGetNotSpecified(cmiKey);
		validatorUndefinedGetValue(cmiKey)
		validatorWriteOnly(cmiKey);
		validatorAdlDataReadSharedDataFalse.run(cmiKey,this);

		/* 4.2.4.1 Completion Status Evaluation */
		if (cmiKey == "cmi.completion_status"){
			if (this.cmi.completion_threshold && this.cmi.progress_measure){
				return parseFloat(this.cmi.progress_measure)>=parseFloat(this.cmi.completion_threshold)?"completed":"incomplete";
			}
			if (this.cmi.completion_threshold){
				return "unknown";
			}
			return this.cmi.completion_status;
		}
		
		/* 4.2.22.1 Success Status Evaluation */
		if (cmiKey == "cmi.success_status"){
			if(this.cmi.scaled_passing_score && this.cmi.score.scaled){
				return parseFloat(this.cmi.score.scaled)>=parseFloat(this.cmi.scaled_passing_score)?"passed":"failed";
			}
			if (this.cmi.scaled_passing_score){
				return "unknown";
			}
			return this.cmi.success_status;
		}
		
		var key = this.expandKey(cmiKey, true);
		var obj = eval("this." + key);
		if (!$type(obj)) {
			throw new ScormError(403,cmiKey);
		}
		try{
			if ($type(obj) == 'function') {
				var fn = obj;
				obj = eval("this." + key.substring(0, key.lastIndexOf('.')));
				return fn.bind(obj).call();
			}
			return obj;
		}catch(e){
			throw new ScormError(301);
		}

	},

	set : function(cmiKey, value) {
		validatorSetNotSpecified(cmiKey);
		validatorUndefinedSetValue(cmiKey)
		validatorReadOnly(cmiKey);
		validatorCmiExitTokens(cmiKey, value);
		validatorSetInteractionsIdBefore.run([cmiKey,value], this);
		validatorInteractionsLearnerResponseAndCorrectResponse.run([cmiKey, value],this);
		validatorAudioCaptioning(cmiKey, value);
		validatorLearnerPreferences(cmiKey,value);
		validatorObjectiveCompletionStatus(cmiKey, value);
		validatorObjectiveSuccessStatus(cmiKey, value);
		validatorObjectivesDependency.run(cmiKey, this);
		validatorAllObjectivesIdNotDuplicated.run( [ cmiKey, value ], this);
		validatorWriteOnceObjectiveID.run([cmiKey,value],this);
		validatorReal_10_7(cmiKey, value);
		validatorUrn(cmiKey,value);
		validatorLocalizedString(cmiKey,value);	
		validatorScoreScaled(cmiKey, value);
		validatorProgressMeasure(cmiKey, value);
		validatorTimestamp(cmiKey, value);
		validatorTimeInterval(cmiKey, value);
		validatorInteractionType(cmiKey, value);
		validatorInteractionsResult(cmiKey, value);
		validatorAdlNavRequest(cmiKey,value);
		validatorAdlDataWriteSharedDataFalse.run(cmiKey,this);
		validatorAdlDataIdNotDuplicated.run([cmiKey,value],this);
		validatorAdlDataDependency.run(cmiKey,this);
		
		var key = "this." + this.expandKey(cmiKey, false);
		if (key.indexOf(".get(") > 0) {
			try {
				var location = key.indexOf(".get(");
				while (location > -1) {
					var toEval = key.substring(0,
							key.indexOf(")", location) + 1);
					eval(toEval);
					location = key.indexOf(".get(", location + 1);
				}
			} catch (err) {
				throw new ScormError(351);
			}
		}
		
		var setVal = function(obj, param)
		{
			obj[param] = value;
		}
		
		var keyObj = key.substr(0, key.lastIndexOf('.'));
		var keyParam = key.substr(key.lastIndexOf('.') + 1);
				
		eval("setVal("+keyObj + ", '"+ keyParam + "')");
		
		/* Sequencing Impacts: cmi.success_status RTE p183 */
//		if (cmiKey=="cmi.success_status" && this.at.current.options.data.item){
//			var i = this.at.current.options.data.item;
//			if (i.sequencing && i.sequencing.objectives && i.sequencing.objectives.primaryObjective && i.sequencing.objectives.primaryObjective.objectiveID!=""){
//				this.at.objectives[i.sequencing.objectives.primaryObjective.objectiveID].setObjectiveProgressStatus(value!="unknown");
//				this.at.objectives[i.sequencing.objectives.primaryObjective.objectiveID].setObjectiveSatisfiedStatus(value=="passed"?true:false);
//			}
//		}

	}

});

var CannotReturnElement = new Class( {
	cannotReturn : true,

	initialize : function() {
	}
});

var Validable = new Class( {

	validable : true,

	validators : new Array(),

	initialize : function() {
	},

	validate : function() {
		this.validators.each(function(validator) {
			validator.validate(this);
		}, this);
	}

});

var RequestValid = new Class({
	Extends: Hash,
	initialize: function(at){
	this.at=at;
	},
	get: function(key){
		var target;
		var m = key.match(/(continue|previous|(choice|jump)\.{target=(.*)})/);
		if(m){
			request = m[2]?m[2]:m[1];
			target = m[3];
			return !!navigationRequest[request].bind(this.at)(target).valid ? "true": "false";
		}
		return "unknown";
		
	}
})

var Collection = new Class( {

	array : [],

	initialize : function() {
	},

	_count : function() {
		return this.array.length;
	},

	get : function(index, isGetter) {
		if (isGetter) {
			if (index >= this.array.length)
				throw new ScormError(301);
		} else {
			if (index == this.array.length)
				this.array[index] = {};
			else if (index > this.array.length)
				throw new ScormError(351);
		}
		return this.array[index];
	},

	_children : []

});

var CommentsFrom = new Class( {

	// Implements : [ Collection, CannotReturnElement, Validable ],
	Implements : Collection,

	initialize : function() {
	},

	_children : [ 'comment', 'location', 'timestamp' ]
});

var Objectives = new Class( {

	Extends : Collection,

	initialize : function() {
		this.array = [];
	},
	/*
	 * Override del metodo get della classe Collection in modo da creare un
	 * nuovo oggetto score ogni volta che viene aggiunto un elemento alla
	 * collezione cmi.objectives. inizializza ad unknow success_status e
	 * completion_status
	 */
	get : function(index, isGetter) {
		var len = this.array.length;
		var toReturn = this.parent.run( [ index, isGetter ], this);
		if (len + 1 == this.array.length) {
			this.array[index].score = new Score();
			this.array[index].success_status="unknown";
			this.array[index].completion_status="unknown";
		}
		return toReturn;
	},
	_children : [ "id", "score", "success_status", "completion_status",
			"progress_measure", "description" ]
});

var Score = new Class( {
	Extends : Hash,
	initialize : function() {
	},
	_children : [ "max", "min", "raw", "scaled" ]
});

var Interactions = new Class({
		Extends : Collection,
		initialize : function() {
			this.array = [];
		},
		/*
		 * Override del metodo get della classe Collection in modo che ogni
		 * volta che viene aggiunto un elemento alla collezione cmi.interactions
		 * vengano istanziate due nuove Collection per
		 * cmi.interactions.n.objectives e cmi.interactions.n.correct_responses
		 */
		get : function(index, isGetter) {
			var len = this.array.length;
			var toReturn = this.parent.run( [ index, isGetter ], this);
			if (len + 1 == this.array.length) {
				this.array[index].objectives = new Collection();
				this.array[index].correct_responses = new Collection();
			}
			return toReturn;
		},
		_children : [ "id", "type", "objectives", "timestamp",
				"correct_responses", "weighting", "learner_response",
				"result", "latency", "description" ]
	});

var Data = new Class({
	Extends: Collection,
	initialize: function(){
		this.array = [];
	},
	_children: ["id","store"]
})