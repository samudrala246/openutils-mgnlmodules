var API = new Class( {
	lastError : 0,
	version: "1.0",
	initialize : function(at,node, sharedDataGlobalToSystem) {
		this.s = new Store(at);
		this.status = NOT_INITIALIZED;
		this.at = at;
		this.node = node;
		this.sharedDataGlobalToSystem = sharedDataGlobalToSystem;
	},
	
	GetValue : function(cmiKey) {
		try {
			validatorGetValueStatus(this.status);
			var r = this.s.get(cmiKey);
			if(cmiKey.indexOf("_children") > 0 && $type(r)=="array" ){
				r = r.join(',');
			}
			//console.info("GetValue",cmiKey," -> ",r)
			this.lastError = 0;
			return r;
		} catch (e) {
			this.lastError = e.errorNumber;
			//console.error(e.message);
			return "";
		}
	},
	
	SetValue : function(cmiKey, value) {
		try {
			validatorSetValueStatus(this.status);
			//console.info("SetValue",cmiKey,value)
			this.s.set(cmiKey, value);
			this.lastError = 0;
			return "true";
		} catch (e) {
			this.lastError = e.errorNumber;
			//console.error(e.message);
			return "false"
		}
	},
	
	GetLastError : function() {
		if(this.lastError){
			//console.info("GetLastError: " + this.lastError);
		}
		return this.lastError;
	},

	GetErrorString : function(input) {
		//console.info("GetErrorString ", input, " ", ErrorMessages[input]);
		return ErrorMessages[input]?ErrorMessages[input]:"";
	},

	GetDiagnostic : function(input) {
		return "";
	},

	Initialize : function(input) {
		try {
			//console.group("Initialize");
			validatorEmptyStringInput(input);
			validatorInitializeStatus(this.status);
			new Request({
				method:'post',
				url: this.at.context+urlPersistence,
				async : false,
				onSuccess:function(response){
					var decodedJson = JSON.decode(response);
					new Hash(decodedJson.cmi).each(function(v,k){
						if (v.array){
							if (k=="interactions"){
								for(var i=0; i< v.array.length; i++){
									this.s.cmi[k]['array'][i] = {};
									for( k2 in v.array[i]){
										if (v.array[i][k2].array){
											this.s.cmi[k]['array'][i][k2] = new Collection();
											this.s.cmi[k]['array'][i][k2]['array'] = v.array[i][k2].array;
										}else{
											this.s.cmi[k]['array'][i][k2] = v.array[i][k2];
										}
									}
								}
							}else{
								this.s.cmi[k]['array'] = v.array;
							}
						}else{
							this.s.cmi[k] = v;
						}
					},this);

					if(decodedJson != null && decodedJson.adl!=null)
						{
					decodedJson.adl.each(function(v,i){
						this.s.adl.data.array.push(new Hash({
							id : v.id,
							store : v.store,
							readSharedData: v.readSharedData,
							writeSharedData: v.writeSharedData
						}));
					},this);
						}
					if (this.s.cmi.entry != "ab-initio"){
						switch (this.s.cmi.exit){
							case "suspend":
							case "logout":
								this.s.cmi.entry = "resume"; break;
							this.s.cmi.entry = ""; break; //else
						}
					}
					this.s.cmi.exit = "";
				}.bind(this),
				onFailure: function(){
					throw new ScormError(102)
				}
			}).post({
				command: 'initialize',
				mgnlPath: path,
				mgnlRepository: 'lms',
				activityId : this.node.getName()
			});
			this.lastError = 0;
			this.status = RUNNING;
			this.at.debounce = false;
			return "true";
		} catch (e) {
			this.lastError = e.errorNumber?e.errorNumber:1000;
			//console.error(e.message?e.message:"Module error",e);
			this.at.debounce = false;
			return "false"
		}
	},

	Terminate : function(input) {
		try {
			this.lastError = 0;
			this.s.cmi.entry = "";
			if (this.s.cmi.total_time && this.s.cmi.session_time){
				this.s.cmi.total_time = new ScormTime(this.s.cmi.total_time).add(new ScormTime(this.s.cmi.session_time)).time;
			}
			//console.info("Terminate", this.node.options.label);
			//console.groupEnd()
			validatorEmptyStringInput(input);
			validatorTerminateStatus(this.status);
			this.lastError = 0;
			this.status = TERMINATE;
			if (this.s.adl.nav.request=='suspendAll'){
				this.s.cmi.exit = "suspend";
			}
			new Request({
				method:'post',
				url: this.at.context+urlPersistence,
				async : false,
				onSuccess: function(response){
					//console.log(response);
				}.bind(this),
				onFailure: function(){
					throw new ScormError(391)
				}
			}).post({
				command: 'terminate',
				values:  JSON.encode(new Hash(this.s.cmi).filter(function(item,key){
					return !/^(completion_threshold|launch_data|max_time_allowed|scaled_passing_score|time_limit_action|version)$/.test(key);
				})).slice(1,-1),
				adldata: JSON.encode(this.s.adl.data.array),
				'cmi.exit': this.s.cmi.exit,
				mgnlPath: path,
				mgnlRepository: 'lms',
				activityId : this.node.getName()
			});
			
			if (this.s.adl.nav.request!='_none_'){
				this.at.overallSequencingProcess(this.s.adl.nav.request);
			}

			//LB questa chiamata richiama il processo di exitall per far parsare correttamente gli stati success e satistied
			// l'alberatura si chiuederà ma va bene cosi.
			this.at.endAttemptProcess(this.at.current);
			
			//LB richiamo il setStatus per memorizzare i dati.
			var current = this.at;
	    	var  json = current.toJSON();
			
	       	new Request({
			  method: 'post',
			  url: this.at.context+urlPersistence,
			  async: false,
			  onSuccess: function(response){
					//console.log(response);
				}.bind(this)
			}).post({
			  courseStatus: json,
			  mgnlPath: path,
			  mgnlRepository: 'lms',
			  command: 'setStatus'
			});
			
			return "true";
		} catch (e) {
			this.lastError = e.errorNumber;
			//console.error(e.message);
			return "false"
		}
	},

	Commit : function(input) {
		try {
			//console.info("Commit");
			validatorEmptyStringInput(input);
			validatorCommitStatus(this.status);
			new Request({
				method:'post',
				url: this.at.context+urlPersistence,
				async : false,
				onSuccess: function(response){
					//console.log(response);
				}.bind(this),
				onFailure: function(){
					throw new ScormError(391)
				}
			}).post({
				command: 'commit',
				values: JSON.encode(new Hash(this.s.cmi).filter(function(item,key){
					return !/^(completion_threshold|launch_data|max_time_allowed|scaled_passing_score|time_limit_action|version)$/.test(key);
				})).slice(1,-1),
				adldata: JSON.encode(this.s.adl.data.array),
				mgnlPath: path,
				mgnlRepository: 'lms',
				activityId : this.node.getName()
			});
			this.lastError = 0;
			return "true";
		} catch (e) {
			this.lastError = e.errorNumber;
			//console.error(e.message);
			return "false"
		}
	},

	_setStatus : function(s) {
		this.status = s;
	},

	_getStore: function(){
		return this.s;
	}
});