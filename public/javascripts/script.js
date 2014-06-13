$(document).ready(function() {
    
    $("#add-key-word-btn").click(function() {
        var words = $(".words").html();
        var newwords = $("#add-word-input").val();
        
        if(words !== "")
        {
        
            if(newwords !== "")
            {
                words += ", " + newwords;
            }
        }
        
        else{
            if(newwords !== "")
            {
                words += "" + newwords;
            }
        }
        $(".words").html(words);
        $("#add-word-input").val("");
        $("#add-word-input").attr("placeholder", "Добави кодова дума");
    });

    $('.popover-object').popover();

    
});