function validatorReadOnly(cmiKey) {
	var regex = /(.*_count$|.*_version|.*_children$|^cmi\.comments_from_lms\..*|^cmi\.credit$|^cmi\.entry$|^cmi\.launch_data$|^cmi\.learner_(id|name)$|^cmi\.mode$|^cmi\.max_time_allowed$|^cmi\.total_time$|^cmi\.time_limit_action$|^cmi\.completion_threshold$|^cmi\.scaled_passing_score$|^adl\.nav\.request_valid\..*|^adl\.data\.\d+\.id)/;
	if (regex.test(cmiKey))
		throw new ScormError(404);
}

function validatorWriteOnly(cmiKey) {
	if (/^(cmi\.exit|cmi\.session_time)$/.test(cmiKey))
		throw new ScormError(405);
}

function validatorCmiExitTokens(cmiKey, value) {
	if (/^cmi\.exit$/.test(cmiKey))
		if (!/^(time-out|suspend|logout|normal)?$/.test(value))
			throw new ScormError(406, cmiKey);
}

function validatorSetNotSpecified(cmiKey) {
	var regex = /^\s*$/;
	if (regex.test(cmiKey))
		throw new ScormError(351);
}

function validatorGetNotSpecified(cmiKey) {
	var regex = /^\s*$/;
	if (regex.test(cmiKey))
		throw new ScormError(301);
}

function validatorUndefinedGetValue(cmiKey) {
	if (!regex.some(function(item) {
		return item.test(cmiKey);
	})) {
		if (regex.some(function(item) {
			return item.test(cmiKey.replace(/\.(_children|_count)$/, ""));
		})) {
			throw new ScormError(301);
		}

		if (m = cmiKey.match(/\.(\d+)\./g)) {
			if (m.some(function(i) {
				return (i.length > 3 && i[1] == "0");
			})) {
				throw new ScormError(301);
			}
		}

		throw new ScormError(401, cmiKey);
	}
}

function validatorUndefinedSetValue(cmiKey) {
	if (!regex.some(function(item) {
		return item.test(cmiKey);
	})) {
		if (regex.some(function(item) {
			return item.test(cmiKey.replace(/\.(_children|_count)$/, ""));
		})) {
			throw new ScormError(351);
		}

		if (m = cmiKey.match(/\.(\d+)\./g)) {
			if (m.some(function(i) {
				return (i.length > 3 && i[1] == "0");
			})) {
				throw new ScormError(351);
			}
		}

		throw new ScormError(401, cmiKey);
	}
}

function validatorUrn(cmiKey, value) {
	if (/^cmi\.(.*)\.id$/.test(cmiKey) && /^urn:/.test(value)) {
		if (!/^urn:[a-z0-9][a-z0-9-]{0,31}:[a-z0-9()+,\-.:=@;$_!*'%\/?#]+$/i
				.test(value)) {
			throw new ScormError(406);
		}
	}
}

function validatorLocalizedString(cmiKey, value) {
	if (/^cmi\.(comments_from_(lms|learner)\.\d+\.comment|(objectives|interactions)\.\d+\.description|learner_name)$/
			.test(cmiKey)) {
		if (/^\{lang=.*\}/.test(value)) {
			if (!/^((\{lang=([a-zA-Z]{2,3}|i|x)(\-[a-zA-Z0-9\-]{2,8})?\}))(.*?)$/
					.test(value)) {
				throw new ScormError(406);
			}
		}
	}
}

function validatorReal_10_7(cmiKey, value) {
	if (/(.*\.weighting|^cmi.learner_preference.audio_level|^cmi.learner_preference.delivery_speed|.+\.score\..+|cmi(\.objectives\.\d+)?\.progress_measure|^cmi.scaled_passing_score)$/
			.test(cmiKey))
		if (!/^-?([0-9]{1,5})(\.[0-9]{1,18})?$/.test(value))
			throw new ScormError(406, cmiKey);
}

function validatorInteractionType(cmiKey, value) {
	if (/^cmi\.interactions\.\d+\.type$/.test(cmiKey))
		if (!/^(true-false|choice|fill-in|long-fill-in|likert|matching|performance|sequencing|numeric|other)$/
				.test(value))
			throw new ScormError(406, cmiKey);
}

function validatorInteractionsResult(cmiKey, value) {
	if (/^cmi\.interactions\.\d+\.result$/.test(cmiKey))
		if (!/^(-?([0-9]{1,5})(\.[0-9]{1,18})?|correct|incorrect|unanticipated|neutral)$/
				.test(value))
			throw new ScormError(406, cmiKey);
}

function validatorInteractionsLearnerResponseAndCorrectResponse(cmiKey, value) {
	if (m = cmiKey.match(/^cmi\.interactions\.(\d+)\..*response/)) {
		var n = cmiKey
				.match(/cmi\.interactions\.\d+\.correct_responses\.(\d+)\.pattern/)
		switch (this.cmi.interactions.array[m[1]].type) {
		case "true-false":
			if (n && n[1] > 0) {
				throw new ScormError(351);
			}
			if (!/^(true|false)$/.test(value)) {
				throw new ScormError(406);
			}
			break;

		case "choice":
			var splitted = value.split("[,]");
			var splittedHash = new Hash();

			for ( var i = 0; i < this.cmi.interactions.array.length; i++) {
				for (j = 0; j < this.cmi.interactions.array[i].correct_responses.array.length; j++) {
					if (this.cmi.interactions.array[i].correct_responses.array[j].pattern == value) {
						throw new ScormError(351);
					}
				}
			}

			if (value != "") {
				for ( var i = 0; i < splitted.length; i++) {
					if (splitted[i] == "" || /[\[\],]/.test(splitted[i])
							|| splittedHash[splitted[i]]) {
						throw new ScormError(406);
					} else {
						splittedHash.set(splitted[i], 1);
					}
				}
			}
			break;
		case "long-fill-in":
			var long = true;
		case "fill-in":
			value.split("}").some(
					function(s, index) {
						if (/^\{case_matters=/.test(s)) {
							if (!/^\{case_matters=(true|false)$/.test(s)) {
								throw new ScormError(406);
							}
						}
						
						/*long-fill-in non ha {order_matters=true|false}*/
						if (!this.long && /^\{order_matters=/.test(s)) {
							if (!this.long
									&& !/^\{order_matters=(true|false)$/
											.test(s)) {
								throw new ScormError(406);
							}
						} else {
							/*
							 * esce dal ciclo quando non c'è un match con
							 * {case_matters= o {order_matters=, l'ultima grafa
							 * non è considerata in quanto eliminata dal metodo
							 * split
							 */
							return true;
						}
					}, {
						long : long
					})

			value
					.split("[,]")
					.each(
							function(s) {
								if (/\{lang=.*\}/.test(s)) {
									if (m = s
											.match(/^((\{lang=([a-zA-Z]{2,3}|i|x)(\-[a-zA-Z0-9\-]{2,8})?\}))(.*?)$/)) {
										if (!validLanguages[m[3]]) {
											throw new ScormError(406);
										}
									}
								}
							});
			break;
		case "likert":
			/*
			 * If the interaction type (cmi.interactions.n.type) is likert then
			 * the LMS shall manage 1 and only 1 correct response pattern.
			 */
			if (n && n[1] > 0) {
				throw new ScormError(351);
			}
			
			if (/(\[[.,]\]|[{}])/.test(value)) {
				throw new ScormError(406);
			}
			
			break;
		case "matching":
			value.split("[,]").each(function(s){
				if (!/^\w+\[\.\]\w+$/.test(s)){
					throw new ScormError(406);	
				}
			});
			break;
		case "performance":
			var splitted;
			if (/^\{order_matters=/.test(value)) {
				if (!/^\{order_matters=(true|false)\}/.test(value)) {
					throw new ScormError(406);
				}
				splitted = value.split("}");
			}
			if (!splitted){
				splitted = value.split("[,]");
			}else{
				splitted = splitted[1].split("[,]");
			}
			
			splitted.each(function(s,index,array){
				if (m = s.match(/^\w*\[\.\](\w+|[-0-9\.]*(\[:\])?[-0-9\.]*)?$/)){
					if (m[2]){
						m[1].split("[:]").each(function(real){
							if (real!="" && !/^-?([0-9]{1,5})(\.[0-9]{1,18})?$/.test(real)){
								throw new ScormError(406);
							}
						})
					}
				}else{
					throw new ScormError(406);
				}
			})
			break;
		case "sequencing":
			if (n && n[1]>this.cmi.interactions.array[m[1]].correct_responses.array.length){
				throw new ScormError(351);
			}
			if (value!="" && !/^\w+(\[,\]\w+)*$/.test(value)){
				throw new ScormError(406);
			}
			break;
		case "numeric":
			if (n && n[1] > 0) {
				throw new ScormError(351);
			}
			
			var splitted = value.split("[:]");
			splitted.each(function(s){
				if (!/^-?([0-9]{1,5})(\.[0-9]{1,18})?$/.test(s)){
					throw new ScormError(406);
				}				
			});
			if (splitted.length>2){
				throw new ScormError(406);
			}else{
				if (splitted.length==2){
					if (parseFloat(splitted[0])>parseFloat(splitted[1])){
						throw new ScormError(406);
					}
				}
			}
			break;
		case "other":
			if (n && n[1] > 0) {
				throw new ScormError(351);
			}
			
			if (value==""){
				throw new ScormError(406);
			}
			break;
		}
	}
}

function validatorTimeInterval(cmiKey, value) {
	if (/(latency|^cmi\.total_time|^cmi\.session_time)$/.test(cmiKey))
		if (!/^P(\d+Y)?(\d+M)?(\d+D)?(T(((\d+H)(\d+M)?(\d+(\.\d{1,2})?S)?)|((\d+M)(\d+(\.\d{1,2})?S)?)|((\d+(\.\d{1,2})?S))))?$/
				.test(value))
			throw new ScormError(406, cmiKey);
}

function validatorTimestamp(cmiKey, value) {
	if (/timestamp$/.test(cmiKey))
		if (!(/(^cmi.max_time_allowed$|^(19[7-9]{1}[0-9]{1}|20[0-2]{1}[0-9]{1}|203[0-8]{1})((-(0[1-9]{1}|1[0-2]{1}))((-(0[1-9]{1}|[1-2]{1}[0-9]{1}|3[0-1]{1}))(T([0-1]{1}[0-9]{1}|2[0-3]{1})((:[0-5]{1}[0-9]{1})((:[0-5]{1}[0-9]{1})((\.[0-9]{1,2})((Z|([+|-]([0-1]{1}[0-9]{1}|2[0-3]{1})))(:[0-5][0-9])?)?)?)?)?)?)?)?$)/)
				.test(value))
			throw new ScormError(406, cmiKey);
}

function validatorEmptyStringInput(input) {
	if (input != "")
		throw new ScormError(201);
}

function validatorInitializeStatus(status) {
	if (status == RUNNING)
		throw new ScormError(103);
	if (status == TERMINATE)
		throw new ScormError(104);
}

function validatorTerminateStatus(status) {
	if (status == NOT_INITIALIZED)
		throw new ScormError(112);
	if (status == TERMINATE)
		throw new ScormError(113);
}

function validatorCommitStatus(status) {
	if (status == NOT_INITIALIZED)
		throw new ScormError(142);
	if (status == TERMINATE)
		throw new ScormError(143);
}

function validatorGetValueStatus(status) {
	if (status == NOT_INITIALIZED)
		throw new ScormError(122);
	if (status == TERMINATE)
		throw new ScormError(123);
}

function validatorSetValueStatus(status) {
	if (status == NOT_INITIALIZED)
		throw new ScormError(132);
	if (status == TERMINATE)
		throw new ScormError(133);
}
/*
 * Controlla che siano soddisfatti i criteri di dipendenze delle collezioni
 */
function validatorSetInteractionsIdBefore(cmiKey,value) {
	/*
	 * Controlla se cmiKey è del tipo cmi.interactions.n controlla se potrebbe
	 * essere un nuovo inserimento e se la chiave è diversa da id, lancia un
	 * errore perchè cmi.interactions.n.id deve essere creata per prima
	 */
	if ((m = cmiKey.match(/cmi\.interactions\.(\d+)\.(.+)/))
			&& this.cmi.interactions.array.length == m[1] && m[2] != "id") {
		throw new ScormError(408);
	}
	 
	 if (m && m[2] == "id" && value==""){
		 throw new ScormError(406);
	 }

	/*
	 * Controlla se la parte finale della chiave (m[2]) è pattern o
	 * learner_response e nel caso non sia presente cmi.interactions.m[1].type
	 * lancia un errore di dipendenza non soddisfatta
	 */
	if (m && this.cmi.interactions.array.length > m[1]
			&& (/^(pattern|learner_response)$/.test(m[2]))
			&& !(this.cmi.interactions.array[m[1]].type))
		throw new ScormError(408);

	/*
	 * Controlla se la chiave è del tipo
	 * cmi.interactions.n.correct_response.m.patter, se sarebbe un inserimento
	 * valido (m<cmi.interactions.n.correct_response._count) e se non è
	 * presente cmi.interactions.m[1].type
	 */
	if (m
			&& this.cmi.interactions.array.length > m[1]
			&& (/correct_responses\.(\d+)\..+/.test(m[2]))
			&& !(this.cmi.interactions.array[m[1]].type)
			&& (n = m[2].match(/correct_responses\.(\d+)\..+/))
			&& (n[1] <= this.cmi.interactions.array[m[1]].correct_responses.array.length)) {
		throw new ScormError(408);
	}
}

function validatorAudioCaptioning(cmiKey, value) {
	if (/^cmi.learner_preference.audio_captioning$/.test(cmiKey))
		if (!/(-1|0|1)/.test(value))
			throw new ScormError(406, cmiKey);
}

function validatorObjectiveCompletionStatus(cmiKey, value) {
	if (/cmi\.objectives\.\d+\.completion_status/.test(cmiKey))
		if (!/(completed|incomplete|not attempted|unknown)/.test(value))
			throw new ScormError(406, cmiKey);
}

function validatorObjectiveSuccessStatus(cmiKey, value) {
	if (/cmi(\.objectives\.\d+)?\.success_status/.test(cmiKey))
		if (!/(passed|failed|unknown)/.test(value))
			throw new ScormError(406, cmiKey);
}

function validatorScoreScaled(cmiKey, value) {
	if (/^cmi(\.objectives\.\d+)?\.score\.scaled$/.test(cmiKey)) {
		if (value < -1.0 || value > 1.0)
			throw new ScormError(407, cmiKey);
	}
}

function validatorProgressMeasure(cmiKey, value) {
	if (/^cmi(\.objectives\.\d+)?\.progress_measure$/.test(cmiKey)) {
		if (value < 0 || value > 1.0)
			throw new ScormError(407, cmiKey);
	}
}

function validatorObjectivesDependency(cmiKey) {
	if ((m = cmiKey.match(/cmi\.objectives\.(\d+)\.(.+)/))
			&& this.cmi.objectives.array.length == m[1] && m[2] != "id")
		throw new ScormError(408);
}

function validatorAllObjectivesIdNotDuplicated(cmiKey, value) {
	var m = cmiKey.match(/cmi\.(interactions\.\d+\.)*objectives\.(\d+)\.id/);
	if (m) {
		/*
		 * aggiunto controllo su id diverso da "" con lancio di errore 406 type
		 * mismatch
		 */
		if (value == "") {
			throw new ScormError(406);
		}
		var a;
		if (m[1]) {
			var n = m[1].match(/interactions\.(\d+)\./);
			a = this.cmi.interactions.array[n[1]].objectives.array;
		} else
			a = this.cmi.objectives.array;
		if (m[2] <= a.length)
			for ( var i = 0; i < a.length; i++) {
				if (a[i].id == value && m[2] != i)
					throw new ScormError(351);
			}
	}
}

function validatorWriteOnceObjectiveID(cmiKey, value) {
	var m = cmiKey.match(/cmi\.objectives\.(\d+)\.id/);

	if (m && parseInt(m[1]) < this.cmi.objectives.array.length
			&& this.cmi.objectives.array[parseInt(m[1])].id != value) {
		throw new ScormError(351);
	}
}

function validatorLearnerPreferences(cmiKey,value){
	if (/^cmi\.learner_preference\./.test(cmiKey)){
		if (/\.(audio_level|delivery_speed)$/.test(cmiKey)){
			if (parseFloat(value)<0){
				throw new ScormError(407);
			}
		}
		if (/\.language$/.test(cmiKey)){
			if (value !="" && !/([a-zA-Z]{2,3}|i|x)(\-[a-zA-Z0-9\-]{2,8})?/.test(value)){
				throw new ScormError(406);
			}
		}
		
	}
}

function validatorAdlDataDependency(cmiKey) {
	if ((m = cmiKey.match(/adl\.data\.(\d+)\.(.+)/))
			&& this.adl.data.array.length == m[1] && m[2] != "id"){
		throw new ScormError(408);
	}
}

function validatorAdlDataIdNotDuplicated(cmiKey, value) {
	if (/adl\.data\.(\d+)\.id/.test(cmiKey)) {
		this.adl.data.array.each(function(d) {
			if (d.id == value) {
				throw new ScormError(351);
			}
		});
	}
}

function validatorAdlDataReadSharedDataFalse(cmiKey) {
	if (m = cmiKey.match(/adl\.data\.(\d+)\.store/)) {
		if (m[1]>=this.adl.data.array.length){
			throw new ScormError(301);
		}
		if (this.adl.data.array[m[1]].readSharedData == "false") {
			throw new ScormError(405);
		}
		if(this.adl.data.array[m[1]].store==""){
			throw new ScormError(403);
		}
	}
}

function validatorAdlDataWriteSharedDataFalse(cmiKey) {
	if (m = cmiKey.match(/adl\.data\.(\d+)\.store/)) {
		if (m[1]>=this.adl.data.array.length){
			throw new ScormError(351);
		}
		if (this.adl.data.array[m[1]].writeSharedData == "false") {
			throw new ScormError(404);
		}
	}
}

function validatorAdlNavRequest(cmiKey, value) {
	if (/adl\.nav\.request/.test(cmiKey)) {
		if (!/^(continue|previous|exit|exitAll|abandon|abandonAll|suspendAll|_none_|{target=.*}(choice|jump))$/
				.test(value))
			throw new ScormError(406);
	}
}

regex = new Hash({
	cmi_comment_learnerRE : /^cmi\.comments_from_learner\.(_children|_count|\b(0|[1-9][0-9]*)\b\.(comment|timestamp|location))$/,
	cmi_comment_lmsRE : /^cmi\.comments_from_lms\.(_children|_count|\b(0|[1-9][0-9]*)\b\.(comment|timestamp|location))$/,
	cmi_childrenRE : /^cmi\._children$/,
	cmi_versionRE : /^cmi\._version$/,
	cmiRE : /^cmi\.(_children|_version|completion_(status|threshold)|credit|entry|exit|launch_data|location|max_time_allowed|mode|progress_measure|scaled_passing_score|session_time|success_status|suspend_data|time_limit_action|total_time)$/,
	cmi_interactionsRE : /^cmi\.interactions\.(_children|_count|\b(0|[1-9][0-9]*)\b\.(id|type|objectives\.(_count|\b(0|[1-9][0-9]*)\b\.id)|timestamp|correct_responses\.(_count|\b(0|[1-9][0-9]*)\b\.pattern)|weighting|learner_response|latency|description|result))$/,
	cmi_learnerRE : /^cmi\.learner(_id|_name|_preference\.(_children|audio_level|language|delivery_speed|audio_captioning))$/,
	cmi_objectivesRE : /^cmi\.objectives\.(_children|_count|\b(0|[1-9][0-9]*)\b\.(id|score\.(_children|scaled|raw|max|min)|success_status|completion_status|progress_measure|description))$/,
	cmi_scoreRE : /^cmi\.score\.(_children|scaled|raw|max|min)$/,
	adl_navRE : /^adl\.nav\.request|adl\.nav\.request_valid\.(continue|previous|(choice|jump)\.{target=.*})$/,
	adl_data : /^adl\.data\.(_children|_count|\b(0|[1-9][0-9]*)\b\.(id|store))$/
});

var validLanguages = {
	'aa' : 'aa',
	'ab' : 'ab',
	'ae' : 'ae',
	'af' : 'af',
	'ak' : 'ak',
	'am' : 'am',
	'an' : 'an',
	'ar' : 'ar',
	'as' : 'as',
	'av' : 'av',
	'ay' : 'ay',
	'az' : 'az',
	'ba' : 'ba',
	'be' : 'be',
	'bg' : 'bg',
	'bh' : 'bh',
	'bi' : 'bi',
	'bm' : 'bm',
	'bn' : 'bn',
	'bo' : 'bo',
	'br' : 'br',
	'bs' : 'bs',
	'ca' : 'ca',
	'ce' : 'ce',
	'ch' : 'ch',
	'co' : 'co',
	'cr' : 'cr',
	'cs' : 'cs',
	'cu' : 'cu',
	'cv' : 'cv',
	'cy' : 'cy',
	'da' : 'da',
	'de' : 'de',
	'dv' : 'dv',
	'dz' : 'dz',
	'ee' : 'ee',
	'el' : 'el',
	'en' : 'en',
	'eo' : 'eo',
	'es' : 'es',
	'et' : 'et',
	'eu' : 'eu',
	'fa' : 'fa',
	'ff' : 'ff',
	'fi' : 'fi',
	'fj' : 'fj',
	'fo' : 'fo',
	'fr' : 'fr',
	'fy' : 'fy',
	'ga' : 'ga',
	'gd' : 'gd',
	'gl' : 'gl',
	'gn' : 'gn',
	'gu' : 'gu',
	'gv' : 'gv',
	'ha' : 'ha',
	'he' : 'he',
	'hi' : 'hi',
	'ho' : 'ho',
	'hr' : 'hr',
	'ht' : 'ht',
	'hu' : 'hu',
	'hy' : 'hy',
	'hz' : 'hz',
	'ia' : 'ia',
	'id' : 'id',
	'ie' : 'ie',
	'ig' : 'ig',
	'ii' : 'ii',
	'ik' : 'ik',
	'io' : 'io',
	'is' : 'is',
	'it' : 'it',
	'iu' : 'iu',
	'ja' : 'ja',
	'jv' : 'jv',
	'ka' : 'ka',
	'kg' : 'kg',
	'ki' : 'ki',
	'kj' : 'kj',
	'kk' : 'kk',
	'kl' : 'kl',
	'km' : 'km',
	'kn' : 'kn',
	'ko' : 'ko',
	'kr' : 'kr',
	'ks' : 'ks',
	'ku' : 'ku',
	'kv' : 'kv',
	'kw' : 'kw',
	'ky' : 'ky',
	'la' : 'la',
	'lb' : 'lb',
	'lg' : 'lg',
	'li' : 'li',
	'ln' : 'ln',
	'lo' : 'lo',
	'lt' : 'lt',
	'lu' : 'lu',
	'lv' : 'lv',
	'mg' : 'mg',
	'mh' : 'mh',
	'mi' : 'mi',
	'mk' : 'mk',
	'ml' : 'ml',
	'mn' : 'mn',
	'mo' : 'mo',
	'mr' : 'mr',
	'ms' : 'ms',
	'mt' : 'mt',
	'my' : 'my',
	'na' : 'na',
	'nb' : 'nb',
	'nd' : 'nd',
	'ne' : 'ne',
	'ng' : 'ng',
	'nl' : 'nl',
	'nn' : 'nn',
	'no' : 'no',
	'nr' : 'nr',
	'nv' : 'nv',
	'ny' : 'ny',
	'oc' : 'oc',
	'oj' : 'oj',
	'om' : 'om',
	'or' : 'or',
	'os' : 'os',
	'pa' : 'pa',
	'pi' : 'pi',
	'pl' : 'pl',
	'ps' : 'ps',
	'pt' : 'pt',
	'qu' : 'qu',
	'rm' : 'rm',
	'rn' : 'rn',
	'ro' : 'ro',
	'ru' : 'ru',
	'rw' : 'rw',
	'sa' : 'sa',
	'sc' : 'sc',
	'sd' : 'sd',
	'se' : 'se',
	'sg' : 'sg',
	'sh' : 'sh',
	'si' : 'si',
	'sk' : 'sk',
	'sl' : 'sl',
	'sm' : 'sm',
	'sn' : 'sn',
	'so' : 'so',
	'sq' : 'sq',
	'sr' : 'sr',
	'ss' : 'ss',
	'st' : 'st',
	'su' : 'su',
	'sv' : 'sv',
	'sw' : 'sw',
	'ta' : 'ta',
	'te' : 'te',
	'tg' : 'tg',
	'th' : 'th',
	'ti' : 'ti',
	'tk' : 'tk',
	'tl' : 'tl',
	'tn' : 'tn',
	'to' : 'to',
	'tr' : 'tr',
	'ts' : 'ts',
	'tt' : 'tt',
	'tw' : 'tw',
	'ty' : 'ty',
	'ug' : 'ug',
	'uk' : 'uk',
	'ur' : 'ur',
	'uz' : 'uz',
	've' : 've',
	'vi' : 'vi',
	'vo' : 'vo',
	'wa' : 'wa',
	'wo' : 'wo',
	'xh' : 'xh',
	'yi' : 'yi',
	'yo' : 'yo',
	'za' : 'za',
	'zh' : 'zh',
	'zu' : 'zu',
	'aar' : 'aar',
	'abk' : 'abk',
	'ave' : 'ave',
	'afr' : 'afr',
	'aka' : 'aka',
	'amh' : 'amh',
	'arg' : 'arg',
	'ara' : 'ara',
	'asm' : 'asm',
	'ava' : 'ava',
	'aym' : 'aym',
	'aze' : 'aze',
	'bak' : 'bak',
	'bel' : 'bel',
	'bul' : 'bul',
	'bih' : 'bih',
	'bis' : 'bis',
	'bam' : 'bam',
	'ben' : 'ben',
	'tib' : 'tib',
	'bod' : 'bod',
	'bre' : 'bre',
	'bos' : 'bos',
	'cat' : 'cat',
	'che' : 'che',
	'cha' : 'cha',
	'cos' : 'cos',
	'cre' : 'cre',
	'cze' : 'cze',
	'ces' : 'ces',
	'chu' : 'chu',
	'chv' : 'chv',
	'wel' : 'wel',
	'cym' : 'cym',
	'dan' : 'dan',
	'ger' : 'ger',
	'deu' : 'deu',
	'div' : 'div',
	'dzo' : 'dzo',
	'ewe' : 'ewe',
	'gre' : 'gre',
	'ell' : 'ell',
	'eng' : 'eng',
	'epo' : 'epo',
	'spa' : 'spa',
	'est' : 'est',
	'baq' : 'baq',
	'eus' : 'eus',
	'per' : 'per',
	'fas' : 'fas',
	'ful' : 'ful',
	'fin' : 'fin',
	'fij' : 'fij',
	'fao' : 'fao',
	'fre' : 'fre',
	'fra' : 'fra',
	'fry' : 'fry',
	'gle' : 'gle',
	'gla' : 'gla',
	'glg' : 'glg',
	'grn' : 'grn',
	'guj' : 'guj',
	'glv' : 'glv',
	'hau' : 'hau',
	'heb' : 'heb',
	'hin' : 'hin',
	'hmo' : 'hmo',
	'hrv' : 'hrv',
	'hat' : 'hat',
	'hun' : 'hun',
	'arm' : 'arm',
	'hye' : 'hye',
	'her' : 'her',
	'ina' : 'ina',
	'ind' : 'ind',
	'ile' : 'ile',
	'ibo' : 'ibo',
	'iii' : 'iii',
	'ipk' : 'ipk',
	'ido' : 'ido',
	'ice' : 'ice',
	'isl' : 'isl',
	'ita' : 'ita',
	'iku' : 'iku',
	'jpn' : 'jpn',
	'jav' : 'jav',
	'geo' : 'geo',
	'kat' : 'kat',
	'kon' : 'kon',
	'kik' : 'kik',
	'kua' : 'kua',
	'kaz' : 'kaz',
	'kal' : 'kal',
	'khm' : 'khm',
	'kan' : 'kan',
	'kor' : 'kor',
	'kau' : 'kau',
	'kas' : 'kas',
	'kur' : 'kur',
	'kom' : 'kom',
	'cor' : 'cor',
	'kir' : 'kir',
	'lat' : 'lat',
	'ltz' : 'ltz',
	'lug' : 'lug',
	'lim' : 'lim',
	'lin' : 'lin',
	'lao' : 'lao',
	'lit' : 'lit',
	'lub' : 'lub',
	'lav' : 'lav',
	'mlg' : 'mlg',
	'mah' : 'mah',
	'mao' : 'mao',
	'mri' : 'mri',
	'mac' : 'mac',
	'mkd' : 'mkd',
	'mal' : 'mal',
	'mon' : 'mon',
	'mol' : 'mol',
	'mar' : 'mar',
	'may' : 'may',
	'msa' : 'msa',
	'mlt' : 'mlt',
	'bur' : 'bur',
	'mya' : 'mya',
	'nau' : 'nau',
	'nob' : 'nob',
	'nde' : 'nde',
	'nep' : 'nep',
	'ndo' : 'ndo',
	'dut' : 'dut',
	'nld' : 'nld',
	'nno' : 'nno',
	'nor' : 'nor',
	'nbl' : 'nbl',
	'nav' : 'nav',
	'nya' : 'nya',
	'oci' : 'oci',
	'oji' : 'oji',
	'orm' : 'orm',
	'ori' : 'ori',
	'oss' : 'oss',
	'pan' : 'pan',
	'pli' : 'pli',
	'pol' : 'pol',
	'pus' : 'pus',
	'por' : 'por',
	'que' : 'que',
	'roh' : 'roh',
	'run' : 'run',
	'rum' : 'rum',
	'ron' : 'ron',
	'rus' : 'rus',
	'kin' : 'kin',
	'san' : 'san',
	'srd' : 'srd',
	'snd' : 'snd',
	'sme' : 'sme',
	'sag' : 'sag',
	'slo' : 'slo',
	'sin' : 'sin',
	'slk' : 'slk',
	'slv' : 'slv',
	'smo' : 'smo',
	'sna' : 'sna',
	'som' : 'som',
	'alb' : 'alb',
	'sqi' : 'sqi',
	'srp' : 'srp',
	'ssw' : 'ssw',
	'sot' : 'sot',
	'sun' : 'sun',
	'swe' : 'swe',
	'swa' : 'swa',
	'tam' : 'tam',
	'tel' : 'tel',
	'tgk' : 'tgk',
	'tha' : 'tha',
	'tir' : 'tir',
	'tuk' : 'tuk',
	'tgl' : 'tgl',
	'tsn' : 'tsn',
	'ton' : 'ton',
	'tur' : 'tur',
	'tso' : 'tso',
	'tat' : 'tat',
	'twi' : 'twi',
	'tah' : 'tah',
	'uig' : 'uig',
	'ukr' : 'ukr',
	'urd' : 'urd',
	'uzb' : 'uzb',
	'ven' : 'ven',
	'vie' : 'vie',
	'vol' : 'vol',
	'wln' : 'wln',
	'wol' : 'wol',
	'xho' : 'xho',
	'yid' : 'yid',
	'yor' : 'yor',
	'zha' : 'zha',
	'chi' : 'chi',
	'zho' : 'zho',
	'zul' : 'zul'
};