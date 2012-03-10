function init(manifest,resources,context) {
  new ScormPlayer(manifest,context,resources)
}

/*
 * Questa classe gestice il layout della pagina
 */

var Layout = new Class({
  initialize: function(sp) {
    this.scormPlayer = sp;
    this.buttons = new Hash();

    new Jx.Layout('container');

    new Jx.Layout('scormHeader', {
      height:50,
    })

    new Jx.Layout('scormTree', {
      top:50,
      width: 300
    });

    new Jx.Layout('scormMain', {
      top: 50,
      left:300,
    });

    // BUTTONS
    this.buttons.previousBtn = new Jx.Button({
      id: 'previousBtn',
      label: 'Previous',
      tooltip: 'Previous',
      onClick: this.scormPlayer.Previous.bind(this.scormPlayer),
    }).addTo('scormHeader');

    this.buttons.continueBtn = new Jx.Button({
      id: 'continueBtn',
      label: 'Continue',
      tooltip: 'Continue to the next activity',
      onClick : this.scormPlayer.Continue.bind(this.scormPlayer),
    }).addTo('scormHeader');

    this.buttons.suspendallBtn = new Jx.Button({
      id: 'suspendallBtn',
      label: 'Suspend All',
      tooltip: 'Suspend current activity',
      onClick: this.scormPlayer.SuspendAll.bind(this.scormPlayer),
    }).addTo('scormHeader');

    this.buttons.exitBtn = new Jx.Button({
      id: 'exitBtn',
      label: 'Exit',
      tooltip: 'Exit current activity',
      onClick: this.scormPlayer.Exit.bind(this.scormPlayer),
    }).addTo('scormHeader');

    this.buttons.exitallBtn = new Jx.Button({
      id: 'exitallBtn',
      label: 'Exit All',
      tooltip: 'Exit all',
      onClick: this.scormPlayer.ExitAll.bind(this.scormPlayer),
    }).addTo('scormHeader');

    this.buttons.abandonallBtn = new Jx.Button({
      id: 'abandonallBtn',
      label: 'Abandon All',
      tooltip: 'Abandon All',
      onClick: this.scormPlayer.AbandonAll.bind(this.scormPlayer),
    }).addTo('scormHeader');

    this.buttons.abandonBtn = new Jx.Button({
      id: 'abandonBtn',
      label: 'Abandon',
      tooltip: 'Abandon',
      onClick: this.scormPlayer.Abandon.bind(this.scormPlayer),
    }).addTo('scormHeader');

    if (debug){
	    new Jx.Button({
	      id: 'invia',
	      label: 'Invia',
	      tooltip: 'invia stato activity tree',
	      onClick: function(){
	        var json = this.scormPlayer.current.toJSON();
	        var r =new Request({
	          method: 'post',
	          url: this.scormPlayer.context+urlPersistence,
	
	        });
	        r.post({
	          courseStatus: json,
	          mgnlPath: path,
	          mgnlRepository: 'lms',
	          command: 'setStatus',
	        });
	      }.bind(this),
	    }).addTo('scormHeader');
	
	    new Jx.Button({
	      id: 'clean',
	      label: 'clean',
	      tooltip: 'invia clean activity tree',
	      onClick: function(){
	        var r =new Request({
	          method: 'post',
	          url: this.scormPlayer.context+urlPersistence,
	        });
	        r.post({
	          courseStatus: "",
	          mgnlPath: path,
	          mgnlRepository: 'lms',
	          command: 'setStatus',
	        });
	      }.bind(this),
	    }).addTo('scormHeader');
	    
	    $("trackingModel").show();
    }

    $('container').resize();
  }
});

/*
 * La classe ScormPlayer inizializza gli activityTree, seleziona quello di
 * default e lo fa partire
 */
var ScormPlayer = new Class({
  initialize: function(manifest, context, resources){
	debug = /debug=true/.test(window.location.search);
    this.layout = new Layout(this);
    this.activityTrees =  new Array();
    this.tree = new ScormTree({parent: 'scormTree',data:null});

    this.context=context;
    this.base = manifest.resources.base;

    /* inizializzazione dell'hash delle risorse */
    this.resources = new Hash();
    manifest.resources.resource.each(function(r) {
      this.resources.set(r.identifier, resources+this.base+r.base+r.href);
    },this);

    /* richiesta ajax per caricare il tracking model da server */
    manifest.organizations.organization.each(function(org,index) {
      var json;
      new Request({
        method: 'post',
        url: this.context+urlPersistence,
        async: false,
        onSuccess:function(response){
          json=response;
        },
        onFailure:function(){
          json = "";
        }
      }).post({
        mgnlPath: path,
        mgnlRepository: 'lms',
        command: 'status',
      });
      var at = new ActivityTree(org, this, index,json, manifest.sequencingCollection);
      this.activityTrees.push(at);
      this.tree.append(at.root);
    },this);

    /* selezione dell'attività di default */
    this.current = this.activityTrees[0];
    this.activityTrees.every(function(at){
      if (at.root.getName()==manifest.organizations["default"]){
        this.current = at;
        return false;
      }else return true;
    },this);

    document.title = this.current.title + " - Scorm Player" ;

    if(debug){
    function recursiveTM(tm,node){
    	var t = node.options.data.track
      tm.grab(new Element('h6',{html:node.options.label}));
      tm.grab(new Element('div',{html:
        "attemptProgressStatus: "+t.getAttemptProgressStatus()+"|old: "+t.previousAttemptInformation.get("_attemptProgressStatus")+"<br/>"+
        "attemptCompletionStatus: "+t.getAttemptCompletionStatus()+"|old: "+t.previousAttemptInformation.get("_attemptCompletionStatus")+"<br/>"}));
      //"activityProgressStatus: "+node.options.data.track.activityProgressStatus+"<br/>"+
      if (node.nodes){
        node.nodes.each(function(n){
          recursiveTM(tm,n);
        });
      }
    }

//    node = this.current.root;
    $("tm").addEvent('click',function(){
      $("contentTM").set('html',"");
      recursiveTM($("contentTM"), this.current.root)
      $("contentTM").highlight('#ddf','#ddf');
    }.bind(this))
    /* per debug */
//      object =new Element('div',{html:"<h5>Objectives</h5>"});
//      $("contentTM").grab(object)
//      this.current.objectives.each(function(obj){
//        object.grab(new Element("h6",{html: obj.id}));
//        $each(obj,function(v,k){
//          if(k!="id" && $type(v)=="boolean"||$type(v)=="string" ){
//            object.appendText(k+": "+v);
//            object.grab(new Element("br"));
//          }
//        },obj)
//      })
    }
    this.current.start();
     window.addEvent('unload', function(){
       var json = this.current.toJSON();
       if(!(new Hash($($('scormFrame').contentDocument.body).getProperties('onUnload','onbeforeunload')).some(function(i){return !!i}))){
      	 if (API_1484_11 && API_1484_11.status==RUNNING){
      		 API_1484_11.Terminate("");
      	 }
       }
        var r =new Request({
          method: 'post',
          url: this.context+urlPersistence,
          async: false,
        });
        r.post({
          courseStatus: json,
          mgnlPath: path,
          mgnlRepository: 'lms',
          command: 'setStatus',
        });
         
         $('scormFrame').src = null;
         this.current.overallSequencingProcess("exitall");
         return true;
     }.bind(this));
  },

  Continue: function(){
    this.current.Continue();
  },
  Previous: function(){
    this.current.Previous();
  },
  SuspendAll: function(){
    this.current.SuspendAll();
  },
  Exit:  function(){
    this.current.Exit();
  },
  ExitAll:  function(){
    this.current.ExitAll();
  },
  AbandonAll:  function(){
    this.current.AbandonAll();
  },
  Abandon:  function(){
    this.current.Abandon();
  }
});

/* funzione per debug da rimuovere */
function mappa(v){
  return v.map(function(node){
    return node.options.label;
  })
}

