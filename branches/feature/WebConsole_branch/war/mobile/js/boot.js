/**
 * This javascript entry of mobile client.
 *
 * auther: handy.wang 2010-07-07
 */
$().ready(function() {
  $("#settings").button({
              icons: {
                  primary: 'ui-icon-gear'
              }
          }).click(function() {
            AppSettings.getInstance().show();
          })
  
  AppSettings.getInstance().show();
  $("#welcome-content-loading").hide();
});