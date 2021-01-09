(function() 
{ // avoid variables ending up in the global scope
	

	// page components
	  var albumDetails, albumsList, imageDetails, numTotImag, imagPerPage = 5, numTotPages, currentPage = 1;
	    pageOrchestrator = new PageOrchestrator(); // main controller

	  window.addEventListener("load", () => {
	    if (sessionStorage.getItem("username") == null) {
	      window.location.href = "index.html";
	    } else {
	      pageOrchestrator.start(); // initialize the components
	      pageOrchestrator.refresh();
	    } // display initial content
	  }, false);
	
	
	
	
	
	////////////////////////////////////
	// Constructors of view components//
	///////////////////////////////////
	
	
	//MESSAGGIO DI BENVENUTO
	
	  function PersonalMessage(_username, messagecontainer) {
	    this.username = _username;
	    this.show = function() {
	      messagecontainer.textContent = this.username;
	    
	    }
	  }	

	//ALBUM LIST
	  
	  function AlbumsList(_alert, _interna1, _interna1body, _interna2Wait, _interna2Selected, _contenitorePrevNext, _id_contenitore_SalvaOrdinamento, _id_avvisoOrdinamentoEsistente) 
	  {
	    this.alert = _alert;
	    this.interna1 = _interna1;
	    this.interna1body = _interna1body;
	    this.interna2Wait= _interna2Wait;
	    this.interna2Selected= _interna2Selected;
	    this.contenitorePrevNext=_contenitorePrevNext;
	    this.id_contenitore_SalvaOrdinamento = _id_contenitore_SalvaOrdinamento;
	    this.id_avvisoOrdinamentoEsistente = _id_avvisoOrdinamentoEsistente;
	    
	 

	    this.reset = function() {
	      this.interna1.style.visibility = "hidden";
	      this.id_avvisoOrdinamentoEsistente.innerHTML = "";
	      this.id_contenitore_SalvaOrdinamento.visibility = "hidden";
	    }
	   
	//    this.show = function(next) {
   this.show = function() {
	      var self = this;
	      
	    
	      makeCall("GET", "GetAlbumListRIA", null,
	        function(req) {
	          if (req.readyState == 4) {
	            var message = req.responseText;
	            if (req.status == 200) {
	              var albumsToShow = JSON.parse(req.responseText);
	              if (albumsToShow.length == 0) {
	                self.alert.textContent = "No albums yet!";
	                return;
	              }
	              self.update(albumsToShow); // self visible by closure
	      //       if (next) next(); // show the default element of the list if present
	            }
	          } 
				if (req.status == 401){
					window.location.href = "index.html";
					window.sessionStorage.removeItem('username');
				}
				
			  else {
	            self.alert.textContent = message;
	          }
	        }
	      );
	    };


	    this.update = function(arrayAlbums) {
	      var row, titlecell, datecell, anchor;
	      this.interna1body.innerHTML = ""; // empty the table body
	      
         
	     var z = 0;
	      
	      // build updated list
	      var self = this;
	      arrayAlbums.forEach(function(album) { // self visible here, not this
	     
	    	row = document.createElement("tr");
	        
	        sortCell = document.createElement("td"); 
	        logoCell = document.createElement("img");
	        sortCell.appendChild(logoCell);
	        logoCell.setAttribute('width', "25");
            logoCell.setAttribute('src', "./Images/LogoMakr_67rKvh.png");
            logoCell.style.pointerEvents = "none";

	        
	        row.appendChild(sortCell);
	  
	        
	        //colonna Titolo inizio//
	        titlecell = document.createElement("td"); 
	        anchor = document.createElement("a");
	        titlecell.appendChild(anchor);
	        linkText = document.createTextNode(album.title);
	        anchor.appendChild(linkText);
	      
	        row.appendChild(titlecell);
	   
            anchor.setAttribute('albumid', album.id); // set a custom HTML attribute
            anchor.setAttribute('id', "idRiga"+z);
            z++;
            anchor.classList.add('rigaSortable');
	        anchor.addEventListener("click", (e) => {
	        	
	        	document.getElementById("id_titoloAlbum").textContent = album.title;
	        // dependency via module parameter
	          albumDetails.show(e.target.getAttribute("albumid")); // the list must know the details container
	          console.log("Hai selezionato l'album con id "+ e.target.getAttribute("albumid")) ; 
	          currentPage=1;
	        }, false);
	        anchor.href = "#";
	        
	        row.appendChild(titlecell);
	     
	        self.interna1body.appendChild(row);
	      //colonna Titolo fine//  
	        
	             
	      //colonna Data inizio//
	        datecell = document.createElement("td");
	        datecell.textContent = album.creationDate;
	        
	        row.appendChild(datecell);
	      
	        
            self.interna1body.appendChild(row);
          //colonna Data fine// 
            
	        
	        
	      });
	      
	      
	      
	      
	      this.interna1.style.visibility = "visible";
	      this.interna1body.style.visibility = "visible";
	      this.interna2Wait.style.visibility = "visible";
	      this.interna2Selected.style.visibility = "hidden";
	      this.contenitorePrevNext.style.visibility = "hidden";
	      this.id_contenitore_SalvaOrdinamento.style.visibility = "visible";
	      
	      //DRaggable table rows
	      
	      makeRowsSortable();
	    	
	      
	      
	      
	      //prendo il valore dell'id di ogni riga
	      var table = document.getElementById("id_interna1"); 
	      
	      //Array dell'ordinamento originale all'avvio dell'applicazione ('nomeArray'+1)
	      var arrrayAlbumIDs1 = []; //array per contenere gli id degli album secondo l'ordinamento
	      var arrayOrder1 = []; // array per contenere la posizione dell'id dell'album
	      
		   for (var r = 0, n = table.rows.length-1; r < n; r++) {
		    		        	
		        	var el = document.getElementById("idRiga"+r); 
		        	
		        	
		        	console.log("Valore riga : "+(r+1));
		        	
		        	arrayOrder1.push(r+1);
		        	
		        	console.log("Valore id : "+el.getAttribute("albumid"));
		        	
		        	let alID = parseInt(el.getAttribute("albumid"));
		        	
		        	arrrayAlbumIDs1.push(alID);

	   
		   }
		   
		 
		   
		   const saveOrder = document.getElementById("id_saveOrder");
		   saveOrder.addEventListener('click', () =>{
			   
			   
			   //Array dell'ordinamento dopo il salvataggio ('nomeArray'+2)
			   var arrrayAlbumIDs2 = [];//svuoto gli array dell'ordinamento nuovo
			   var arrayOrder2 = [];//svuoto gli array dell'ordinamento nuovo
			   
			   
				   
				   
				
				   
				   //array con tutte le righe con classe "rigaSortable"
				   var righe = document.getElementsByClassName("rigaSortable");
				   
				   for (var r = 0, n = table.rows.length-1; r < n; r++) {
					   
					   var el = righe[r]; //prendo la prima riga dell'array con classe "rigaSortable"
   		        	
			        	
			        	el.setAttribute('id', "idRiga"+r); //cambio il suo attributo idRiga con il numero della nuovo riga
			        	
			        	console.log("Valore riga : "+(r+1)); 
			        	
			        	arrayOrder2.push(r+1); //inserisco nell'array dell'ordinamento il valore della riga
			        	
			        	console.log("Valore id : "+el.getAttribute("albumid"));
			        	
			        	let alID = parseInt(el.getAttribute("albumid"));
		         
			        	arrrayAlbumIDs2.push(alID); //inserisco nell'array degli album la nuova posizione dell'id
		   
			   }
				   
			//confronto, se il nuovo ordinamento, è uguale a quello già impostato, lo notifico,
				   //altrimenti chiamo la servlet per salvarlo
				var arrayUguali = (arrrayAlbumIDs1.length == arrrayAlbumIDs2.length) && arrrayAlbumIDs1.every(function(element, index) {
				    return element === arrrayAlbumIDs2[index]; 
				});
				console.log("Sono diversi? "+arrrayAlbumIDs1+" VS "+arrrayAlbumIDs2);
				if(arrayUguali) //ordinamento salvato uguale al precedente
					{
					console.log("Gli array sono uguali");
					self.id_avvisoOrdinamentoEsistente.textContent = "Non è stato variato l'ordinamento";
					}
				else //ordinamento salvato diverso dal precedente, chiamo la servlet
					{
					 // self.id_avvisoOrdinamentoEsistente.innerHTML = "";
					  self.id_avvisoOrdinamentoEsistente.textContent = "Ordinamento salvato!";
					  arrrayAlbumIDs1 = arrrayAlbumIDs2; //ripristino il valore dell'ordinamento standard per succesiva verifica
					  console.log("Gli array sono diversi");
					  var a = escape(JSON.stringify(arrayOrder2)); //Stringify + Escape Character
					  var b = escape(JSON.stringify(arrrayAlbumIDs2));
				 
					   makeCall("POST", 'SaveOrderRIA?arrayOrd='+a+"&arrayAlb="+b, null,
						        function(req) {
			   
					          if (req.readyState == 4) {
					            var message = req.responseText;
					            if(req.status == 200) {

					           // self.show(albId, imPath); 
					            	console.log("Richiesta inviata alla servlet");
					            	
					            }

								if (req.status == 401){
										window.location.href = "index.html";
										window.sessionStorage.removeItem('username');
								}
					            	
			      
					          }  	else {
					        	  		self.alert.textContent = message;
				          			}
						   		}
					   );
					
					
					
					}
				
				
				   
	        	
		   });
			   

		   
	      
	      
	    }
	        
	

	  }	  
	  
	  
	//ALBUM DETAILS
	    
	    function AlbumDetails (_alert, _interna2Selected, _interna2Wait, _intro_attesa_selezione,
	    		_intro_dopo_selezione, _contenitorePrevNext, _nextButton, _prevButton) {
	    	
	    	this.alert = _alert;
		    this.interna2Wait= _interna2Wait;
		    this.interna2Selected= _interna2Selected;
		    this.intro_attesa_selezione= _intro_attesa_selezione;
		    this.intro_dopo_selezione= _intro_dopo_selezione;
		    this.contenitorePrevNext=_contenitorePrevNext;
		    this.nextButton=_nextButton;
		    this.prevButton=_prevButton;
		  
		    var albummmid;
		    
		  
		    
		    this.reset = function() {
			     this.interna2Selected.style.visibility = "hidden";
			     this.interna2Wait.style.visibility = "hidden";
			     this.prevButton.style.visibility = "hidden";
			     this.nextButton.style.visibility = "hidden";
			    }
		    
	//    this.show = function(albumid, next) {
		    this.show = function(albumid) {
		      var self = this;
		      albummmid = albumid;
		      
		      makeCall("GET", "GetAlbumDetailsRIA?albumid=" + albumid, null,
		        function(req) {
			console.log("risposta : "+req.status);
		          if (req.readyState == 4) {
		            var message = req.responseText;
		            if (req.status == 200) {
		             
		              var imagesToShow = JSON.parse(req.responseText);
		              numTotImag = imagesToShow.length;
		              numTotPages = Math.ceil(numTotImag*1.0 / imagPerPage);
		              
		              
		              
		              console.log("Quante immagini ? "+numTotImag);
		              console.log("Quante pagine in tutto ? "+numTotPages);
		          
		              
		              self.update(imagesToShow); // self is the object on which the function
		        //      	if (next) next();
		        
		              
		             }

					if (req.status == 401){
						window.location.href = "index.html";
						window.sessionStorage.removeItem('username');
					}
				
				
		            } else {
		              self.alert.textContent = message;
		          }
		        }
		      
		     );
		    };
		    
		    
		    this.update = function(imm) {
	    	var row, imagecell, anchor;
		      this.interna2Selected.innerHTML = ""; // empty the table body
		      // build updated list
		      var self = this;
		      
		      row = document.createElement("tr");
		      
		  	
		     //Display Previous Link
		      if(currentPage!=1)
		    	  {
		    	  this.prevButton.style.visibility = "visible";
		    	  }  else this.prevButton.style.visibility = "hidden";
		    //Display Next Link
		      if(currentPage < numTotPages)
		    	  {
		    	  this.nextButton.style.visibility = "visible";
		    	  } else if (currentPage === numTotPages)
		    		  {
		    		  this.nextButton.style.visibility = "hidden";
		    	  }
		      
			
		      
		     //CASO <=5		      
		      if (currentPage === numTotPages && currentPage === 1)
		      { 
		    	  this.nextButton.style.visibility = "hidden";
		    	  var start = 0; 
		    	  var finish = numTotImag;
		      }

		    //CASO pagina finale
		      if(currentPage === numTotPages && currentPage != 1) 
     		  { 
		          this.nextButton.style.visibility = "hidden";
		          var start = (currentPage-1) * imagPerPage;
		    	  var finish = numTotImag;
              } 
		      
		     //CASO in mezzo		
		      		
	          if(currentPage < numTotPages)
	          {
	        	  var start = (currentPage-1) * imagPerPage;
		    	  var finish = currentPage * imagPerPage;
	          }  
		    	  
				      
	          		for (var j = start; j < finish; j++)
				    	  {
				    	 
				    	  var immagine = imm[j];
				    	  	
				    	  	imagecell = document.createElement("td");
				    	  	
				    	  	imagecell.textContent = immagine.titleIm;
				    	  	
				    	  	breakline = document.createElement("br");
				    	  	
				    	  	
				    	  	
				    	  	 imagetag =  document.createElement("img");
				    	  	
				    	  	anchor = document.createElement("a");
				    	  	anchor.appendChild(imagetag);
				    	  	imagecell.appendChild(breakline);
					        
					        imagecell.appendChild(anchor);
					        
					        linkText = document.createTextNode(immagine.path);
					        anchor.appendChild(linkText); 
					        imagetag.appendChild(linkText);


					        
					        imagetag.setAttribute('width', '125');
					        imagetag.setAttribute('src', immagine.path);
					        
					        
					        
					        anchor.setAttribute('imageid', immagine.path); // set a custom HTML attribute
					        anchor.addEventListener("mouseover", (e) => {    
					     //   dependency via module parameter
					        imageDetails.show(albummmid, e.currentTarget.getAttribute("imageid")); // the list must know the details container
						 //      currentPage=1;
							   console.log("Hai selezionato l'immagine con percorso "+ e.currentTarget.getAttribute("imageid")) ; 
					        }, false);
					        anchor.href = "#";
					        
					        
					        
					        
					        row.appendChild(imagecell); 
					        self.interna2Selected.appendChild(row);
				    	  }
		    	  
		      
		      
		
		 
		      this.interna2Wait.style.visibility = "hidden";
		      this.interna2Selected.style.visibility = "visible";
		      this.intro_attesa_selezione.style.visibility = "hidden";
		      this.intro_dopo_selezione.style.visibility = "visible";
		   
		    	
		    } //fine this.update dell'album details
		    
		    
		    this.registerEvents = function(orchestrator)
		    {
		    	
		    	//manage next button
		    	this.nextButton.addEventListener('click', () => {
			        
		    		currentPage++;
		    		albumDetails.show(albummmid);
			      });
		    	
		    	//manage prev button
		    	this.prevButton.addEventListener('click', () => {
			        
		    		currentPage--;
		    		albumDetails.show(albummmid);
			      });
		    	
		    }
	    	
	    	
	    	
	    }
	    
	    
	  //IMAGE DETAILS
	    function ImageDetails (_alert, _id_modalDaMostrare, _id_contenitore_dettagli_IM, _id_imagDaMostrare,
	    		_id_titolo_imm, _id_data_imm, _id_descr_imm, _id_table_commenti, _id_inserisci_commento)
	    {
	    	this.alert = _alert;
	    	this.id_modalDaMostrare = _id_modalDaMostrare;
	    	this.id_contenitore_dettagli_IM = _id_contenitore_dettagli_IM;
	    	this.id_imagDaMostrare = _id_imagDaMostrare;
	    	this.id_titolo_imm = _id_titolo_imm;
	    	this.id_data_imm = _id_data_imm;
	    	this.id_descr_imm = _id_descr_imm;
	    	this.id_table_commenti = _id_table_commenti;
	    	this.id_inserisci_commento = _id_inserisci_commento;
	    	
	    	var albId;
	    	var imPath;
	    	 
	    	
	    	 this.reset = function() 
	    	 {
	    		 this.alert.style.visibility = "hidden";
	    		 this.id_modalDaMostrare.style.visibility = "hidden";
	    		 this.id_contenitore_dettagli_IM.style.visibility = "hidden";
	 	    	 this.id_imagDaMostrare.style.visibility = "hidden";
	  	    	 this.id_titolo_imm.style.visibility = "hidden";
	 	    	 this.id_data_imm.style.visibility = "hidden";
	 	    	 this.id_descr_imm.style.visibility = "hidden";
	 	    	 this.id_table_commenti.style.visibility = "hidden";
	 	    	 this.id_inserisci_commento.style.visibility = "hidden";
			    
			     
			 }
	    	 
	    	 
	    	 this.show = function ( albumid, path)
	    	 {
	    		 var self = this;
	    		 albId = albumid;
	    		 imPath = path;
	    		 
	    		 console.log("dal js parte questa get: GeImageDetailsRIA?albumid=" + albumid + "&percorso=" + path);
	    		 
	    		 makeCall("GET", "GeImageDetailsRIA?albumid=" + albumid + "&percorso=" + path, null,
	    				 function(req){
	    			 if (req.readyState == 4) {
	 		            var message = req.responseText;
			            if (req.status == 200) {
			            	
			             
			              var imageCommentJson = JSON.parse(req.responseText);
			              
			              var imageToFocus = imageCommentJson[0];       
			              
			              var imageComments = imageCommentJson[1];
			              
			              
			              console.log("che immagine ha preso nel js? "+imageToFocus.titleIm);
			              console.log("Quanti commenti dell'immagine "+imageComments.length);
			          
			            
			             self.update(imageToFocus, imageComments, imageComments.length); // self is the object on which the function
			           //  if (next) next();
			        
			              
			             }

					if (req.status == 401){
						window.location.href = "index.html";
						window.sessionStorage.removeItem('username');
					}
				
			            } else {
			              self.alert.textContent = message;
			          }
			        }
			      
			     );
			    };
			   
			    
			    this.update = function(immagineDaMostrare, commentiImmagine, numeroCommenti) {
		   
			       this.id_modalDaMostrare.innerHTML = ""; //
			      	
			       const modal = document.querySelector(".modal");
		    	

			       tableExt = document.createElement("table");
			       bodyExt = document.createElement("tbody");
			       
			       //IMMAGINE - DESCRIZIONE			       
			       row1 = document.createElement("tr");
			       data1 = document.createElement("td");
			       data2 = document.createElement("td");
			       imagTag = document.createElement("img");
			       titTag = document.createElement("p");
			       dataTag = document.createElement("p");
			       descrTag = document.createElement("p");
			       
			       modal.appendChild(tableExt);
			       tableExt.appendChild(bodyExt);	       
			       data1.appendChild(imagTag);		     
			       data2.appendChild(titTag);
			       data2.appendChild(dataTag);
			       data2.appendChild(descrTag);
		       
			       imagTag.setAttribute('width', '450');
			       imagTag.setAttribute('src', immagineDaMostrare.path);
			       titTag.textContent = (immagineDaMostrare.titleIm);
		    	   dataTag.textContent = (immagineDaMostrare.dateIm);
		    	   descrTag.textContent = (immagineDaMostrare.descriptionIm);
		    	   titTag.textContent = (immagineDaMostrare.titleIm);
		    	   dataTag.textContent = (immagineDaMostrare.dateIm);
		    	   descrTag.textContent = (immagineDaMostrare.descriptionIm);
		    	   
		    	   
			       
			       //TABELLA COMMENTI - FORM AGGIUNGI COMMENTO
		    	   //TABELLA COMMENTI
			       row2 = document.createElement("tr");
			       data12 = document.createElement("td");
			       data22 = document.createElement("td");
			       tableComm = document.createElement("table");
			       tableCommHead = document.createElement("thead");
			       tableCommR1 = document.createElement("tr");
			       tableCommH1 = document.createElement("th");
			       tableCommH2 = document.createElement("th");
			       tableCommBody = document.createElement("tbody"); // ciclo

			       
			       if (numeroCommenti >0)
			       {
				       data12.appendChild(tableComm);
				       tableComm.appendChild(tableCommHead);
				       tableCommH1.textContent = ("User");
				       tableCommH2.textContent = ("Commento Inserito");
				       tableCommR1.appendChild(tableCommH1);
				       tableCommR1.appendChild(tableCommH2);
				       tableCommHead.appendChild(tableCommR1); //fino a qui intestaz. tabella commenti
				       tableComm.appendChild(tableCommBody);
				       
				       
				       
				       commentiImmagine.forEach(function(comment) { 
				    	   tableCommR2 = document.createElement("tr");
				    	   tableCommUserData = document.createElement("td");
				    	   tableCommUserData.textContent = comment.userNameComment;
				    	   tableCommR2.appendChild(tableCommUserData);
				    	   tableCommBody.appendChild(tableCommR2);
				    	   
				    	   tableCommTextData = document.createElement("td");
				    	   tableCommTextData.textContent = comment.commentoString;
				    	   tableCommR2.appendChild(tableCommTextData);
				    	   tableCommBody.appendChild(tableCommR2);
				    	   
				    	   tableCommTextData.classList.add('modalText3');
				    	   tableCommUserData.classList.add('modalText');
				       });
			       } 
			       
			       if (numeroCommenti === 0)
			    	   {
			    	   nessuCommento = document.createElement("p");
			    	   nessuCommento.textContent = "Ancora nessun commento inserito per questa immagine";
			    	   nessuCommento.classList.add('modalText4');
			    	   data12.appendChild(nessuCommento);
			    	   }
			       
			       
			       
			       
			       //FORM AGGIUNGI COMMENTO
			       form = document.createElement("form");
			       fieldset = document.createElement("fieldsetr");
			       pTagAggComm = document.createElement("p");
			       pTagErroreCommVuoto = document.createElement("p");
			       input1 = document.createElement("textarea");
			       input2 = document.createElement("input");
			       br = document.createElement("br");
			       
			    
			       input1.name = "commento";			       
			       pTagAggComm.textContent = "Inserisci un commento";
			       
			       pTagAggComm.appendChild(br);
			       pTagAggComm.appendChild(input1);
			       fieldset.appendChild(pTagAggComm);
			       form.appendChild(fieldset);
			       
			       data22.appendChild(form);
			       form.setAttribute("action","#");
			    	   
			       input2.type = "button";
			       input2.value = "Invia";
			       input1.classList.add("textField");
			       input1.setAttribute("required", "true");
			       form.appendChild(input2);
			       
			       
			       
			       pTagErroreCommVuoto.textContent = "Errore nell'invio del commento";
			      
			       data22.appendChild(pTagErroreCommVuoto);
			       
			       
			       
			       //imposta classe per colore testo da css
			       titTag.classList.add('modalText');
		    	   dataTag.classList.add('modalText');
		    	   descrTag.classList.add('modalText');
		    	   
		    	  
		    	   
		    	   
		    	   
		    	   tableCommH1.classList.add('modalText2');
			       tableCommH2.classList.add('modalText2');
			       pTagAggComm.classList.add('modalText2');
			       pTagErroreCommVuoto.classList.add('modalText5');
			       
			       

		    	   bodyExt.appendChild(row1);
		    	   bodyExt.appendChild(row2);
		    	   row1.appendChild(data1);
		    	   row1.appendChild(data2);
		    	   row2.appendChild(data12);
		    	   row2.appendChild(data22);
		    	   pTagErroreCommVuoto.style.visibility = "hidden";
			        
		    	   var self = this;
		    	   
		    		
	    		  
		    	   //raccolata input commento
	    		   input2.addEventListener('click', (e) =>{
		        		
	    			   var form = e.target.closest("form");
	    			   
	    			   if (form.checkValidity()) {
	    				   
	    				   console.log("Tasto premuto");
	    				   var commentoInserito = input1.value;
	    				   console.log("commento inserito: "+commentoInserito);
	    				   
	    				   makeCall("POST", 'AddCommentRIA?commm='+commentoInserito+"&albumid="+albId+"&percorso="+imPath, e.target.closest("form"),
    					        function(req) {
	    					   
	    					
	    					   
    					          if (req.readyState == 4) {
    					            var message = req.responseText;
    					            if(req.status == 200) {
    
    					            self.show(albId, imPath); 
    					            	
    					            }

									if (req.status == 401){
										window.location.href = "index.html";
										window.sessionStorage.removeItem('username');
									}
    					            	
				      
    					    }  else {
    					    	
    					    	
    					    	pTagErroreCommVuoto.style.visibility = "visible";
    					    	self.alert.textContent = message;
    					    	

	    			   		}
	    				   }
	    				   );
				        	
				        } else {
					    	form.reportValidity();
				        }
	    		   });
		    		
			     
			       
			       
			       
			        
			       //apertura modal
			        document.getElementById("id_modalDaMostrare").classList.add('open');
		      
			        //opzioni chiusura modal
			        modal.addEventListener('click', (e) =>{
			        	if(e.target.classList.contains('modal')){
		        		modal.classList.remove('open');
			        	}
			        });
			        document.addEventListener("keydown", ({key}) =>{
			        	if(key ==="Escape"){
			        		modal.classList.remove('open');
			        	}
			        	
			        });
			        
			        
			        
			        this.alert.style.visibility = "visible";
		    		 this.id_modalDaMostrare.style.visibility = "visible";
		    		 this.id_contenitore_dettagli_IM.style.visibility = "visible";
		 	    	 this.id_imagDaMostrare.style.visibility = "visible";
		  	    	 this.id_titolo_imm.style.visibility = "visible";
		 	    	 this.id_data_imm.style.visibility = "visible";
		 	    	 this.id_descr_imm.style.visibility = "visible";
		 	    	 this.id_table_commenti.style.visibility = "visible";
		 	    	 this.id_inserisci_commento.style.visibility = "visible";
		 	    	 
		 	    	 
			    
			    
			    }
	    		 
	    	
	    	
	    }
	  
	  

 	    ///////////////////////////////////////////////////
		/////////////Page Orchestrator INIZIO//////////////
		///////////////////////////////////////////////////
		  
	function PageOrchestrator() {
			 var alertContainer = document.getElementById("id_alert");
			
			 
		/////////////Page Orchestrator START////////
			 this.start = function() {
				 var usrn = sessionStorage.getItem('username');
				 
				 //personal message (in START Page Orchestrator)
			      personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
			        document.getElementById("id_username"));
			      personalMessage.show();
			      
			      
			      //albumList message (in START Page Orchestrator)
				    albumsList = new AlbumsList(alertContainer,
				        document.getElementById("id_interna1"),
				        document.getElementById("id_interna1_body"),
				    	document.getElementById("id_interna2WAIT"),
						document.getElementById("id_interna2SELECTED"),
						document.getElementById("contenitorePrevNext"),
						document.getElementById("id_contenitore_SalvaOrdinamento"),
						document.getElementById("id_avvisoOrdinamentoEsistente")
						);
						
				    
				  //albumList message (in START Page Orchestrator)
				    albumDetails = new AlbumDetails(alertContainer,
				    	document.getElementById("id_interna2SELECTED"),
						document.getElementById("id_interna2WAIT"),
						document.getElementById("intro_attesa_selezione"),
						document.getElementById("intro_dopo_selezione"),
						document.getElementById("contenitorePrevNext"),
						document.getElementById("id_nextButton"),
						document.getElementById("id_prevButton"));
			    
				    	albumDetails.registerEvents(this);
				    
				  //imageDetails message (in START Page Orchestrator)
				    imageDetails = new ImageDetails(alertContainer,
				    		document.getElementById("id_modalDaMostrare"),
				    		document.getElementById("id_contenitore_dettagli_IM"),
				    		document.getElementById("id_imagDaMostrare"),
				    		document.getElementById("id_titolo_imm"),
				    		document.getElementById("id_data_imm"),
				    		document.getElementById("id_descr_imm"),
				    		document.getElementById("id_table_commenti"),
				    		document.getElementById("id_inserisci_commento"));
				    
				    document.querySelector("a[href='LogoutRIA']").addEventListener('click', () => {
				        window.sessionStorage.removeItem('username');
				      }) 
				  
				    
			 };
			 
		/////////////Page Orchestrator REFRESH//////// 
			 this.refresh = function() {
			      alertContainer.textContent = "";
			      albumsList.reset();
			      albumDetails.reset();
			      imageDetails.reset();
			    
			     
			      albumsList.show(); // closure preserves visibility of this
			   
			     
			    };
			    
	
		    
			 
			 
		}
		//***********Page Orchestrator FINE***************//


	  
		  
	
	
})();
