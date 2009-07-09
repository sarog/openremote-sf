/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 3.0 of the
 * License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public License along with this software;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston,
 * MA 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/*
 * TODO
 *
 * @author <a href="mailto:">Allen Wei</a>
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
var KNXController = function() {

	return {
         /**
         * Show create knx button dialog.
         */
         showCreateKNXDialog:function() {
            $("#create_KNX_dialog").showModalForm("Create KNX", {
                buttons:{
                    'Create': KNXController.confirmCreate
                },
                confirmButtonName:'Create',
				        width:350
            });
        },

        /**
         * Invoked when user confirm create knx button.
         */
    		confirmCreate:function() {
	          var label = $("#knx_label_input");
	          var groupAddress = $("#knx_group_address_input");
            var command = $("#knx_command_input");

            $("#knx_form").validate({

                invalidHandler:function(form, validator) {
                    $("#create_KNX_dialog").errorTips(validator);
                },

                showErrors:function(){},

                rules: {
                    knx_label_input: {
                        required: true,
                        maxlength: 50
                    },
                    knx_group_address_input: {
                        required: true,
                        maxlength: 11
                    },
                    knx_command_input: {
                        required: true,
                        maxlength: 5
                    }
                },

                messages:{
                    knx_label_input: {
                        required: "Please input a label",
                        maxlength: "Please input a label no more than 50 characters"
                    },
                    knx_group_address_input: {
                        required: "Please input a group address",
                        maxlength: "Please input a group address no more than 11 characters"
                    },
                    knx_command_input: {
                        required: "Please enter a KNX command (ON/OFF).",
                        maxlength: "KNX command can be at most 5 characters long."
                    }
                }
            });

            if ($("#knx_form").valid()) {
	            var knx = new KNX();
	            knx.id = global.BUTTONID++;
	            knx.label = label.val();
	            knx.groupAddress = groupAddress.val();
              knx.command = command.val();

              KNXController.createKNX(knx);
		
	            $("#create_KNX_dialog").closeModalForm();
	          }
	        },

          createKNX: function(knx){
			      var knxView = new KNXView(knx);

			      knx.addDeleteListener(knxView);
			      knx.addUpdateListener(knxView);
			
			      var btn = knxView.getElement();
			
			      makeBtnDraggable(btn);
			
	          btn.inspectable();
		      }
		
	      };
}();

var X10Controller = function() {

  return {

       /**
       * Show create x10 button dialog.
       */
      showCreateX10Dialog:function () {
          $("#create_x10_dialog").showModalForm("Create X10", {
              buttons:{
                  'Create': X10Controller.confirmCreate
              },
              confirmButtonName:'Create',
              width:350
          });
      },

      /**
	     * Invoked when user confirm create x10 button.
	     */
	    confirmCreate:function () {
	        var label = $("#x10_label_input");
	        var address = $("#x10_address_input");
	        var command = $("#x10_command_input");
	        
            $("#x10_form").validate({
                invalidHandler:function(form, validator) {
                    $("#create_x10_dialog").errorTips(validator);
                },

                showErrors:function(){},

                rules: {
                    x10_label_input: {
                        required: true,
                        maxlength: 50
                    },
                    x10_address_input: {
                        required:true,
                        maxlength: 50
                    },
                    x10_command_input: {
                        required:true,
                        maxlength: 50
                    }
                },

                messages:{
                    x10_label_input: {
                        required: "Please input a label",
                        maxlength: "Please input a label no more than 50 charactors"
                    },
                    x10_address_input: {
                        required: "Please input a address",
                        maxlength: "Please input a address no more than 50 charactors"
                    },
                    x10_command_input: {
                        required: "Please input a command",
                        maxlength: "Please input a command no more than 50 charactors"
                    }
                }
            });

            if ($("#x10_form").valid()) {
                var x10 = new X10();
                x10.id = global.BUTTONID++;
                x10.label = label.val();
                x10.address = address.val();
                x10.command = command.val();

                X10Controller.createX10(x10);

                $("#create_x10_dialog").closeModalForm();
            }
	    },

      createX10: function(x10){
			  var x10View = new X10View(x10);
	
			  x10.addDeleteListener(x10View);
			  x10.addUpdateListener(x10View);
			
			  var btn = x10View.getElement();
			  makeBtnDraggable(btn);
	      btn.inspectable();
		  }
	};
}();



var HTTPController = function() {

  return {
        showCreateHTTPDialog:function () {
            $("#create_http_dialog").showModalForm("Create HTTP", {
                buttons:{
                    'Create': HTTPController.confirmCreate
                },
                confirmButtonName:'Create',
			        	width:350
            });
        },

  	    confirmCreate:function () {
        
	        var label = $("#http_label_input");
	        var url = $("#http_url_input");
	        
          $("#http_form").validate({
              invalidHandler:function(form, validator) {
                  $("#create_http_dialog").errorTips(validator);
              },

              showErrors:function(){},

              rules: {
                  http_label_input: {
                      required: true,
                      maxlength: 50
                  },
                  http_url_input: {
                      required:true,
                      maxlength: 1024
                  }
              },

              messages:{
                  http_label_input: {
                      required: "Please input a label",
                      maxlength: "Please input a label no more than 50 characters"
                  },
                  http_url_input: {
                      required: "Please input a URL",
                      maxlength: "Please input a URL no more than 1024 characters"
                  }
              }
          });
            
	        if ($("#http_form").valid()) {
	            var http = new HTTP();
	            http.id = global.BUTTONID++;
	            http.label = label.val();
	            http.url = url.val();

              HTTPController.createHTTP(http);
		
	            $("#create_http_dialog").closeModalForm();
	        }
	    },

      createHTTP: function(http){
			  var httpView = new HTTPView(http);
	
			  http.addDeleteListener(httpView);
			  http.addUpdateListener(httpView);
			
			  var btn = httpView.getElement();
			  makeBtnDraggable(btn);
        btn.inspectable();
		  }
	};
}();

var TCPController = function() {

	return {

         /**
         * Show create TCP/IP button dialog.
         */
         showCreateTCPDialog:function() {
            $("#create_tcp_dialog").showModalForm("Create TCP", {
                buttons:{
                    'Create': TCPController.confirmCreate
                },
                confirmButtonName:'Create',
				        width:350
            });
        },

        /**
         * Invoked when user confirms creating of TCP/IP button.
         */
    		confirmCreate:function() {
	          var label = $("#tcp_label_input");
	          var ip = $("#tcp_ip_input");
            var port = $("#tcp_port_input");
            var command = $("#tcp_command_input");

            $("#tcp_form").validate({

                invalidHandler:function(form, validator) {
                    $("#create_tcp_dialog").errorTips(validator);
                },

                showErrors:function(){},

                rules: {
                    tcp_label_input: {
                        required: true,
                        maxlength: 50
                    },
                    tcp_ip_input: {
                        required: true,
                        maxlength: 1024
                    },
                    tcp_port_input: {
                        required: true,
                        maxlength: 5
                    },
                    tcp_command_input: {
                        required: true,
                        maxlength: 1024
                    }
                },

                messages:{
                    tcp_label_input: {
                        required: "Please input a label",
                        maxlength: "Please input a label no more than 50 characters"
                    },
                    tcp_ip_input: {
                        required: "Please input IP address",
                        maxlength: "Please input IP no more than 1024 characters"
                    },
                    tcp_port_input: {
                        required: "Please input IP port",
                        maxlength: "Please input IP port no more than 5 characters"
                    },
                    tcp_command_input: {
                        required: "Please enter a command",
                        maxlength: "Command can be at most 1024 characters long."
                    }
                }
            });

            if ($("#tcp_form").valid()) {
	            var tcp = new TCP();
	            tcp.id = global.BUTTONID++;
	            tcp.label = label.val();
	            tcp.ip = ip.val();
              tcp.port = port.val();
              tcp.command = command.val();

              TCPController.createTCP(tcp);

	            $("#create_tcp_dialog").closeModalForm();
	          }
	        },

          createTCP: function(tcp){
			      var tcpView = new TCPView(tcp);

			      tcp.addDeleteListener(tcpView);
			      tcp.addUpdateListener(tcpView);

			      var btn = tcpView.getElement();

			      makeBtnDraggable(btn);

	          btn.inspectable();
		      }

	      };
}();

var TelnetController = function() {

	return {
         /**
         * Show create telnet button dialog.
         */
         showCreateTelnetDialog:function() {
            $("#create_telnet_dialog").showModalForm("Create Telnet", {
                buttons:{
                    'Create': TelnetController.confirmCreate
                },
                confirmButtonName:'Create',
				        width:350
            });
        },

        /**
         * Invoked when user confirm create telnet button.
         */
    		confirmCreate:function() {
	          var label = $("#telnet_label_input");
	          var ip = $("#telnet_ip_input");
            var port = $("#telnet_port_input");
            var command = $("#telnet_command_input");

            $("#telnet_form").validate({

                invalidHandler:function(form, validator) {
                    $("#create_telnet_dialog").errorTips(validator);
                },

                showErrors:function(){},

                rules: {
                    telnet_label_input: {
                        required: true,
                        maxlength: 50
                    },
                    telnet_ip_input: {
                        required: true,
                        maxlength: 1024
                    },
                    telnet_port_input: {
                        required: true,
                        maxlength: 5
                    },
                    telnet_command_input: {
                        required: true,
                        maxlength: 1024
                    }
                },

                messages:{
                    telnet_label_input: {
                        required: "Please input a label",
                        maxlength: "Please input a label no more than 50 characters"
                    },
                    telnet_ip_input: {
                        required: "Please input a IP address",
                        maxlength: "Please input a IP address no more than 1024 characters"
                    },
                    telnet_port_input: {
                        required: "Please input IP port",
                        maxlength: "Please input an IP port no more than 5 characters"
                    },
                    telnet_command_input: {
                        required: "Please enter a telnet command.",
                        maxlength: "Telnet command can be at most 1024 characters long."
                    }
                }
            });

            if ($("#telnet_form").valid()) {
	            var telnet = new Telnet();
	            telnet.id = global.BUTTONID++;
	            telnet.label = label.val();
	            telnet.ip = ip.val();
              telnet.port = port.val();
              telnet.command = command.val();

              TelnetController.createTelnet(telnet);

	            $("#create_telnet_dialog").closeModalForm();
	          }
	        },

          createTelnet: function(telnet){
			      var telnetView = new TelnetView(telnet);

			      telnet.addDeleteListener(telnetView);
			      telnet.addUpdateListener(telnetView);

			      var btn = telnetView.getElement();

			      makeBtnDraggable(btn);

	          btn.inspectable();
		      }

	      };
}();



