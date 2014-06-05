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


    // datatable
    $('#usersTable').dataTable();
    $('#tableTests').dataTable();
    $('#tableGamebooks').dataTable();

    $('.dataTables_wrapper input, .dataTables_wrapper select').addClass('form-control').css({'width':'auto','display':'inline'});

    $('.dataTables_wrapper .paginate_button').addClass('btn btn-default');
    
});