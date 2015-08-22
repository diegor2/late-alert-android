package net.startapi.latealert;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.Duration;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;

/***
 * 
 * @author Jhony Moreno
 *
 */
public class DistanceMatrixServices {

	/***
	 * @author Jhony Moreno
	 * @param origins        Latitude e Longitude da Origem
	 * @param destinations  Endereco do Destino
	 * @return				Tempo em segundos do tempo de duracao ate chegar ao local
	 */
	public Duration getDurationMatrix(LatLng origins, String destinations) {
		
		/***
		 * Incluir
		 *  <dependency>
		 *   <groupId>com.google.maps</groupId>
		 *   <artifactId>google-maps-services</artifactId>
		 *   <version>(insert latest version)</version>
		 *	</dependency>
		 */

		//Instancia o contexto do Servi�o, informando a KEY definida no Projeto Late-Alert
		GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyC_Px0Tu5yBXYPgQEj_2Yvlc1ciTuoJxw4");
	    GeocodingResult[] results;		
		DistanceMatrix matrix;
		Duration duration = new Duration();
		
		try {
			
			//Transforma o destino que � um endereco em cordenadas
			results = GeocodingApi.newRequest(context).address(destinations).await();
			
			LatLng destinationsLL = new LatLng(results[0].geometry.location.lat,results[0].geometry.location.lng);	

			matrix  = DistanceMatrixApi.newRequest(context)
					.origins(origins)
					.destinations(destinationsLL)
					.mode(TravelMode.DRIVING)
					.language("pt-BR")
					.awaitIgnoreError();
			
			//Considerando que teremos apenas uma origem e destino , recupera o tempo em Segundos 
			duration = matrix.rows[0].elements[0].duration;

		} catch (Exception e) {
			// TODO: Logar a exception
			e.printStackTrace();
		}
		return duration;
	}

}
