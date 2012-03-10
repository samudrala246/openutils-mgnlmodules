
function mgnlTreeMenuItemConditionSelectedNotScormNode(tree)
    {
    this.tree = tree;

    this.test = function(){
        if (this.tree.selectedNode.itemType!="mgnl:course") return true;
        else return false;
    }
}

function mgnlTreeMenuItemConditionSelectedScormNode(tree)
    {
    this.tree = tree;

    this.test = function(){
        if (this.tree.selectedNode.itemType!="mgnl:course") return false;
        else return true;
    }
}

