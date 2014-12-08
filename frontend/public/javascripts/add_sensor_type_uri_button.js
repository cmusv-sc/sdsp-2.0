$(document).ready(function() {

  console.log("foo")

  var wrapper         = $(".sensorTypeUris"); //Fields wrapper
  var add_button      = $(".add_sensor_type_uri_button"); //Add button ID

  var x = 1; //initial text box count
  $(add_button).click(function(e){ //on add input button click
    e.preventDefault();
    $(wrapper).append('<dl class=" " + id="sensorTypeUri' + x + '_field">'
                      + '<input type="text" id="sensorTypeUri' + x + '" name="sensorTypeUri' + x + '" value="">'
                      + '<a href="#" class="remove_field">&nbsp Remove</a></div>'
                    + '</dl>'); // add input box
    x++; //text box increment
  });

  $(wrapper).on("click",".remove_field", function(e){ //user click on remove text
    e.preventDefault(); $(this).parent('dl').remove(); x--;
  })
});
