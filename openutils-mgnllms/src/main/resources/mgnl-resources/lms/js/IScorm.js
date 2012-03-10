var IScorm = new Class({
	
	getControlMode: function(){
		var temp = {
			choice: true,
			choiceExit: true,
			flow: false,
			forwardOnly: false,
			useCurrentAttemptObjectiveInfo: true,
			useCurrentAttemptProgressInfo: true
		};
		if (this.options.data.item.sequencing){
			return new Hash(this.options.data.item.sequencing.controlMode).combine(temp);
		}else {
			return temp;
		}
	},
	selectNode: function(){
		if (this.owner)
			this.owner.nodes.each(function(node) {
				if (!node.options.enabled)
					node.domObj.getElement('a').addClass('jxDisabled');
			},this);
		this.domObj.getElement('a').addClass('jxTreeItemSelected');
		this.domObj.getElement('a').removeClass('jxDisabled');
		
		this.options.data.pause.dispose();
		this.domObj.getElement('a').grab(this.options.data.play);
		
	},
	deselectNode: function(){
		this.domObj.getElement('a').removeClass('jxTreeItemSelected');
		this.options.data.pause.dispose();
		this.options.data.play.dispose();
    },
    disable: function() {
    	this.options.enabled=false;
   		this.domLabel.fade(0.4);
    },
    enable: function() {
    	this.options.enabled=true;
    	this.domLabel.fade('show');
    },
    addItem: function(item,at,index,seqCollection) {
    	defaulter(item,seqCollection);
    	var node;
    	var options = {
    			label: item.title,
    			open:true
    	};
		if (item.item && item.item.length == 0){
			node = new ScormTreeItem(options);
		}
		else{
			node = new ScormTreeFolder(options);
			item.item.each(function(i,index) {
				node.addItem(i,at,index,seqCollection);
			});
		}
		
		var options = {
				onClick: function(node){
					if (!at.debounce){
						if (this.options.enabled){
							at.debounce = true;
							at.overallSequencingProcess('choice',node);
						}
					}
				},
				data: {
					index:index,
					item:item,
					play: new Element('img',{
						src: contextPath + ".resources/lms/icons/play.png"
					}),
					pause: new Element('img',{
						src: contextPath + ".resources/lms/icons/pause.png"
					}),
				},
		};
		
    	node.setOptions(options);
    	node.options.data.track = new TrackingModel(node,at);
		this.append(node);
	},
	
	availableChildren : function(){
		if (!this.ac){
			if (this.nodes){
				this.ac = this.nodes.filter(function(n){ return true});
			}else{
				this.ac = [];
			}
		}
		return this.ac;
	},
	
	pathFromRoot: function(){
		var actPath = new Array();
		for (var n=this; n.$family.name!="Jx.Tree"; n=n.owner){
			actPath.push(n);
		}
		return actPath.reverse();
	},
	
	next: function(){
		var i=0;
		var found=null;
		var v = this.owner.availableChildren();
		do{
			i++;
			if (v.contains(this.owner.nodes[this.options.data.index+i]))
				found = this.owner.nodes[this.options.data.index+i]
		}while(!found && this.options.data.index+i<this.owner.nodes.length);
		return found;
	},
		
	prev: function(){
		var i=0;
		var found=false;
		var v = this.owner.availableChildren();
		do{
			i++;
			if (v.contains(this.owner.nodes[this.options.data.index-i]))
				found = this.owner.nodes[this.options.data.index-i];
		}while(!found && this.options.data.index-i == 0);
		return found;
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
	findChild: function(path){
		return null;
	}
});

var ScormTreeFolder= new Class({
	Extends: Jx.TreeFolder,
	Implements: IScorm,
	
	initialize: function(options){
		this.parent(options);
	},
	
	selected: function(e,ignoreEn) {
		this.options.ignoreEn=ignoreEn;
		this.parent();
	},
	
	clicked: function(e) {
	},
	
	getName: function() {
		return this.options.data.item.identifier;
	},
	findChild: function(path){
		var f = this.parent(path);
		if (!f){
			this.nodes.some(function(n){
				if (n.nodes){
					f = n.findChild(path);
				}
				return !!f;
			});
		}
		return f;
	}
});

var ScormTree = new Class({
	Extends: Jx.Tree,
	Implements: IScorm,
});

function defaulter (item,seqCollection){
	/*
	 * inserimento dei valori di default di deliveryControls nel caso siano null
	 */
	
	/* 2.1.2. Using Sequencing Collections*/
	if (item.sequencing && item.sequencing.IDRef!=null){
		var seq = item.sequencing.IDRef;

		$each(seq,function(v,k){
			if (v!=null && k!="ID"){
				if(!$type(v)=="array" || v.length>0 || $type(v)=="object"){
					if(item.sequencing[k]==null){
						item.sequencing[k]=v;
					}							
				}
			}
		});
	}
	
	if (!item.sequencing) item.sequencing = {};
	if (!item.sequencing.deliveryControls) item.sequencing.deliveryControls = {};  
	
	item.sequencing.deliveryControls = {
		tracked: item.sequencing.deliveryControls.tracked == 
			null ? true: item.sequencing.deliveryControls.tracked,
		completionSetByContent: item.sequencing.deliveryControls.completionSetByContent == 
			null ? false: item.sequencing.deliveryControls.completionSetByContent,
		objectiveSetByContent: item.sequencing.deliveryControls.objectiveSetByContent == 
			null ? false: item.sequencing.deliveryControls.objectiveSetByContent,
	};
	
	/*
	 * inserimento dei valori di default di completionThreshold nel caso siano null
	 */
	if (!item.completionThreshold) item.completionThreshold = {};
	item.completionThreshold = {
		completedByMeasure: !item.completionThreshold.completedByMeasure ? false : item.completionThreshold.completedByMeasure,
		minProgressMeasure: !item.completionThreshold.minProgressMeasure ? 1.0   : item.completionThreshold.minProgressMeasure,
		progressWeight: 	!item.completionThreshold.progressWeight	 ? 1.0	 : item.completionThreshold.progressWeight, 
	};
	
	
	if (!item.sequencing.constrainedChoiceConsiderations) item.sequencing.constrainedChoiceConsiderations = {};
	item.sequencing.constrainedChoiceConsiderations = {
		preventActivation : !item.sequencing.constrainedChoiceConsiderations.preventActivation ? false : item.sequencing.constrainedChoiceConsiderations.preventActivation,
		constrainChoice : !item.sequencing.constrainedChoiceConsiderations.constrainChoice ? false : item.sequencing.constrainedChoiceConsiderations.constrainChoice,
	};
	
	if (!item.sequencing.rollupRules) item.sequencing.rollupRules ={};
	item.sequencing.rollupRules={
			rollupObjectiveSatisfied: !item.sequencing.rollupRules.rollupObjectiveSatisfied  ? true: item.sequencing.rollupRules.rollupObjectiveSatisfied,
			rollupProgressCompletion: !item.sequencing.rollupRules.rollupProgressCompletion  ? true: item.sequencing.rollupRules.rollupProgressCompletion,
			objectiveMeasureWeight: !item.sequencing.rollupRules.objectiveMeasureWeight  ? 1.0000: item.sequencing.rollupRules.objectiveMeasureWeight,
			rollupRule: !item.sequencing.rollupRules.rollupRule ? [] : item.sequencing.rollupRules.rollupRule,
	};
	
	if (!item.sequencing.rollupConsiderations) item.sequencing.rollupConsiderations = {};
	var rc = item.sequencing.rollupConsiderations;
	item.sequencing.rollupConsiderations = {
		requiredForSatisfied: !rc.requiredForSatisfied ?'always': rc.requiredForSatisfied,
		requiredForNotSatisfied: !rc.requiredForNotSatisfied ? 'always': rc.requiredForNotSatisfied,
		requiredForCompleted: !rc.requiredForCompleted ? 'always' : rc.requiredForCompleted,
		requiredForIncompleted: !rc.requiredForIncompleted ? 'always' : rc.requiredForIncompleted,
		measureSatisfactionIfActive: rc.measureSatisfactionIfActive ? rc.measureSatisfactionIfActive : true
	}
}