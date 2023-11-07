package com.gestion.concecionario.controlador;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.concecionario.excepciones.ResourceNotFoundException;
import com.gestion.concecionario.modelo.Cliente;
import com.gestion.concecionario.modelo.Localidad;
import com.gestion.concecionario.repository.ClienteRepositorio;
import com.gestion.concecionario.repository.LocalidadRepositorio;

@RestController
@RequestMapping("/api/v1/")
@CrossOrigin(origins = "http://localhost:4200/")
public class ClienteControlador {
	
	List<Cliente> clientesActivos;
	List<Cliente> clientesInactivos;

	@Autowired
	private ClienteRepositorio repositorioCli;
	
	@Autowired
	private LocalidadRepositorio repositorioLoc;
	
	Localidad localidad= new Localidad();
	
	//Metodo backend el cual permite traer todos los clientes con el status Activo 
	@GetMapping("/cliente")
	public List<Cliente> listadoClientes(){
		List<Cliente> clientes= repositorioCli.findAll();
		clientesActivos=new ArrayList<Cliente>();
		for(int i=0;i<clientes.size();i++) {
			
			if(clientes.get(i).getStatus().equals("Activo")) {
				clientesActivos.add(clientes.get(i));
			}else {
				new ResourceNotFoundException("El cliente esta Desactivado:");
			}
		}
		
		return clientesActivos;
	}
	
	//Metodo backend el cual permite traer todos los clientes con el status Desactivado 
	@GetMapping("/clienteDes")
	public List<Cliente> listadoClientesDes(){
		List<Cliente> clientes= repositorioCli.findAll();
		clientesInactivos=new ArrayList<Cliente>();
		for(int i=0;i<clientes.size();i++) {
			
			if(clientes.get(i).getStatus().equals("Desactivado")) {
				clientesInactivos.add(clientes.get(i));
			}else {
				new ResourceNotFoundException("El cliente esta Activo:");
			}
		}
		
		return clientesInactivos;
	}
	
	
	
	
	//Metodo backend  el cual permite traer todas las localidades de la BD
	@GetMapping("/localidad")
	public List<Localidad> listadoLocalidades(){
		return repositorioLoc.findAll();
		
	}
	
	//Metodo backend el caul permite resgitrar un nuevo cliente
    //recibe un parametro id el cual permite buscar la localidad a la que pertenece 
	@PostMapping("/cliente/{id}")
	public Cliente guardarCliente(@RequestBody Cliente cliente,@PathVariable Long id) {
		cliente.setStatus("Activo");
		Localidad localidad=repositorioLoc.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe esta localidad con el ID:"+id)) ;
		cliente.setLocalidad(localidad);
		return repositorioCli.save(cliente);
	}
	
	
	//Metodo backend el caul permite traer solo un cliente por id
	//recibe un parametro id el cual permite buscar al cliente 

	@GetMapping("/cliente/{id}")
	public ResponseEntity<Cliente> buscarCliente(@PathVariable Long id){
		Cliente cliente=repositorioCli.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe este cliente con el ID:"+id)) ;
		return ResponseEntity.ok(cliente);
		
	}
	
	//Metodo backend el cual permite actualizar la informacion de un cliente existente en la bd
	//recibe un parametro id el cual permite buscar al cliente 
	@PutMapping("/cliente/{id}")
	public ResponseEntity<Cliente> actualizarCliente(@PathVariable Long id,@RequestBody Cliente clienteModificar){
		Cliente cliente=repositorioCli.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe este cliente con el ID:"+id)) ;
		
		cliente.setNombre(clienteModificar.getNombre());
		cliente.setApellido(clienteModificar.getApellido());
		cliente.setCorreo(clienteModificar.getCorreo());
		cliente.setStatus(clienteModificar.getStatus());
		Localidad localidad=repositorioLoc.findById(clienteModificar.getLocalidad().getId()).orElseThrow(() -> new ResourceNotFoundException("No existe esta localidad con el ID:"+id)) ;
		cliente.setLocalidad(localidad);
		
		Cliente clienteActualizado=repositorioCli.save(cliente);
		return ResponseEntity.ok(clienteActualizado);
		
	}

	
	//Metodo backend el cual modifica el parametro status del cliente lo pone en Desactivado
	//recibe un parametro id el cual permite buscar al cliente 
	@DeleteMapping("/cliente/{id}")
	public ResponseEntity<Map<String,Boolean>> desactivarCliente(@PathVariable Long id){
		Cliente cliente=repositorioCli.findById(id).orElseThrow(() -> new ResourceNotFoundException("No existe este cliente con el ID:"+id)) ;
        cliente.setStatus("Desactivado");	
        repositorioCli.save(cliente);
	Map<String,Boolean> response= new HashMap<>();
	response.put("Desactivado", true);
	return ResponseEntity.ok(response);
	}

}
