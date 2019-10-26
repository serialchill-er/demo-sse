function loadComments () {

    this.source = null;

    this.start = function () {


        var id=getRandomInt(5).toString();

        console.log("Id: "+id);
        this.source = new EventSource("/stream/"+id);

        this.source.addEventListener(id, function (event) {

            // These events are JSON, so parsing and DOM fiddling are needed
            var comment = JSON.parse(event.data);
	        console.log(comment);
/*        var commentTable = document.getElementById("messages");
commentTable.innerHTML+=comment.name+"<br/>";*/


        });

        this.source.onerror = function () {
            this.close();
        };

    };

    this.stop = function() {
        this.source.close();
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
