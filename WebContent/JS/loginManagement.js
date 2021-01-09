/**
 * Login management
 */

(function() { // avoid variables ending up in the global scope

	var input1 = document.getElementById("id_emailREG");
	var input2 = document.getElementById("id_usernREG");
	var input3 = document.getElementById("id_psw1");
	var input4 = document.getElementById("id_psw2");
	
	
 //LOGIN1 with button click
  document.getElementById("loginbutton").addEventListener('click',  (e) => {
	  submitLogin(e);
   
  });
  
  //LOGIN2 with enter key pressed
  document.getElementById("id_loginContainer").addEventListener('keydown', (e) => {
	  if(e.key === 'Enter')
	 
		 {
		 	submitLogin(e);
		 }
	 
   
  });
  
  function submitLogin(e){

  var form = e.target.closest("form");
  if (form.checkValidity()) {
    makeCall("POST", 'CheckLoginRIA', e.target.closest("form"),
      function(req) {
        if (req.readyState == XMLHttpRequest.DONE) {
          var message = req.responseText;
          switch (req.status) {
            case 200:
          	sessionStorage.setItem('username', message);
              window.location.href = "HomePageRIA.html";
              break;
            case 400: // bad request
              document.getElementById("errormessage").textContent = message;
              break;
            case 401: // unauthorized
                document.getElementById("errormessage").textContent = message;
                break;
            case 500: // server error
          	document.getElementById("errormessage").textContent = message;
              break;
          }
        }
      }
    );
  } else {
  	 form.reportValidity();
  }};
  
  
  //show Signup Form when clicking on SignUp href
  document.getElementById("id_sign_up").addEventListener('click', () => {
	    	
	  		document.getElementById("id_signupContainer").style.visibility = "visible";
	  		document.forms["id_fromSignup"].reset();
	  });

  

  
  //SIGNUP1 with button click
  document.getElementById("signupbutton").addEventListener('click',  (e) => {
	  submitSignup(e);
   
  });
  
  //SIGNUP2 with enter key pressed
  document.getElementById("id_signupContainer").addEventListener('keydown', (e) => {
	  if(e.key === 'Enter')
	 
		 {
		  submitSignup(e);
		 }
	 
   
  });
  
  

function submitSignup(e)
{
    var form = e.target.closest("form");
    
    if (form.checkValidity()) {
 
    	var emailInserita = input1.value;
    	var usernameInserito = input2.value;
    	var psw1inserita = input3.value;
    	var psw2inserita = input4.value;
    	
    	
    	if (emailInserita == undefined || usernameInserito == undefined || psw1inserita === undefined || psw2inserita === undefined)
    		{
    			emailInserita = null;
    			usernameInserito = null;
    			psw1inserita = null;
    			psw2inserita = null;
    		}
    	
    	console.log("email inserita "+emailInserita);
    	console.log("user inserito "+usernameInserito);
    	console.log("psw1 inserita "+psw1inserita);
    	console.log("psw2 inserita "+psw2inserita);
    	
    	if (psw1inserita != psw2inserita)
    		{
    			document.getElementById("errormessage2").textContent = "Le due password non corrispondono";
    			return;
    			
    		} 
    			
  	
	        makeCall("POST", 'AddNewUserRIA?email='+emailInserita+"&username="+usernameInserito+"&pwd1="+psw1inserita+"&pwd2="+psw2inserita, e.target.closest("form"),
	        function(req) {
	          if (req.readyState == XMLHttpRequest.DONE) {
	            var message = req.responseText;
	            
	            
	            
	            switch (req.status) {
	              case 200:
	            	  document.getElementById("errormessage2").textContent = "Registrazione avvenuta con successo, effettua il Log In";
	                break;
	              case 400: // bad request
	                document.getElementById("errormessage2").textContent = message;
	                break;
	              case 401: // unauthorized
	                  document.getElementById("errormessage2").textContent = message;
	                  break;
	              case 500: // server error
	            	document.getElementById("errormessage2").textContent = message;
	                break;
	            }
	          }
	        });
    			
    } else {
    	 form.reportValidity();
    }};


})();