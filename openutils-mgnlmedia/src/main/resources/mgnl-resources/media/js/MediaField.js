var MediaField = Ext.extend(Ext.form.TriggerField, {

  triggerClass: 'x-form-media-trigger',

  mediaType: '',

  onTriggerClick: function(){
    if (this.disabled) return;

    window.setNewMedia = function(nodeid, uuid, file, thumb){
      this.setValue(uuid);
    }.createDelegate(this);
    mgnlOpenWindow('/.magnolia/pages/mediaBrowser.html?nodeid=' + name + '&selectMedia=true&mediaType=' + this.mediaType + '&mgnlCK=' + mgnlGetCacheKiller(), 840, 560);
  }

});
