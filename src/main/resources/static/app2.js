function loadComments () {

    this.source = null;

    this.start = function () {


        var id=getRandomInt(3).toString();

        console.log("Id: "+id);
        var source = new EventSource("/stream/"+id);
        var subscribed=document.getElementById("subscribed");
        subscribed.innerHTML+="Subscribed to event ID: "+id+"<br/>";
        setTimeout(function(){
                console.log("Timeout!");
                var id2=getRandomInt(3).toString();

                console.log("Id: "+id2);
                var source2 = new EventSource("/stream/"+id2);

                subscribed.innerHTML+="Subscribed to event ID: "+id2+"<br/>";
                        source2.addEventListener(id2, function (event) {

                            // These events are JSON, so parsing and DOM fiddling are needed
                            var comment = JSON.parse(event.data);
                	        console.log(comment);
                            var commentTable = document.getElementById("messages");
                            commentTable.innerHTML+="<li>"+comment.id+" "+comment.name+"</li><br/>";


                        });

                        source2.onerror = function () {
                            this.close();
                        };

        }, 10000);

        source.addEventListener(id, function (event) {

            // These events are JSON, so parsing and DOM fiddling are needed
            var comment = JSON.parse(event.data);
	        console.log(comment);
            var commentTable = document.getElementById("messages");
            commentTable.innerHTML+="<li>"+comment.id+" "+comment.name+"</li><br/>";


        });

        source.onerror = function () {
            this.close();
        };

    };

    this.stop = function() {
        source.close();
    }

}

comment = new loadComments();

function getRandomInt(max) {
  return Math.floor(Math.random() * Math.floor(max));
}

/*
 * Register callbacks for starting and stopping the SSE controller.
 */
window.onload = function() {
    comment.start();
};
window.onbeforeunload = function() {
    comment.stop();
}
