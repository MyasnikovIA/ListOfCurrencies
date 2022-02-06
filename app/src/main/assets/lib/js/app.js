        var selectVal = "";

        if (typeof(Android)==='undefined') {
            Android = {};
            Android.alert = function(msg){
                alert(msg);
            }
        }

        var roundRub = function(rub) {
           return rub.toFixed(2);
        }

        var calcVal = function() {
            var valVal = +$('#inputVal').val();
            if (valVal < roundRub(selectVal['Nominal'])) {
               $('#inputVal').val(selectVal['Nominal']);
            }
            var _resultVal = ((+$('#inputVal').val()/+selectVal['Nominal']) * selectVal['Value']);
            $('#resultVal').val(roundRub(_resultVal));
        }

        var calckWin = function(objOne) {
           selectVal = objOne;
           $('#inputVal').val(objOne['Nominal']);
           $('#resultVal').val(roundRub(objOne['Value']));
           $("#inputVal").attr({"min" :objOne['Nominal'] });
           $("#dialog").dialog({ modal: true });
        }

        var copyResultToClipboard = function(_dom) {
            _dom.select();
            document.execCommand("copy");
            Android.alert("Результат скопирован в буфер обмена");
        }

        $( function() {
                 for (var key in cbr['Valute']) {
                     var objOne = cbr['Valute'][key];
                     $("#accordion").append(`
                          <div class="clsTitleVal" > ${objOne['Name']} ( ${objOne['CharCode']} ) </div>
                          <center class="bodyVal">
                             <span style="font-size: 16px;"> Соимость ${objOne['Value']} руб. за  ${objOne['Nominal']} единиц.</span><br/>
                             <button class="ui-button ui-widget ui-corner-all" onclick='calckWin(${JSON.stringify(objOne)});'>калькулятор</button>
                          <center>
                      ` );
                  }
                  $("#accordion").accordion();

                  $("#filterVal").on("keyup", function() {
                     var findText = $(this).val().toLowerCase();
                     $(".clsTitleVal").each(function(index) {
                       var valueVal = $( this ).text().toLowerCase();
                       if (valueVal.indexOf(findText) > -1) {
                          $(this).css("display","block");
                       } else {
                          $(this).css("display","none");
                       }
                     });
                     $(".bodyVal").each(function(index) {
                        $(this).css("display","none");
                     })
                  });
        });

