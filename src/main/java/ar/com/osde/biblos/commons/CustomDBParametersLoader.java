package ar.com.osde.biblos.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//import java.util.Random;
import java.util.UUID;

import com.dotmarketing.common.db.DotConnect;
import com.dotmarketing.util.Logger;

public class CustomDBParametersLoader {

	private static final String syncUsuariosPermisos = "syncUsuariosPermisos";
	//private static final Integer delay = 15000;

	public static boolean syncUsuariosPermisos() {
		String hash = UUID.randomUUID().toString();

		lockSyncUsuariosPermisos(hash);

		String hashSync = getCustomParam(syncUsuariosPermisos);

		return hash.equals(hashSync);
	}

	private static void lockSyncUsuariosPermisos(String hash) {
		DotConnect dc = new DotConnect();

		try {

			String query = "SET XACT_ABORT ON " //
					+ "begin transaction " //
					+ "DECLARE @valor varchar(MAX); " //
					+ "SELECT @valor = [value] FROM CUSTOM_PARAMETERS WHERE ID = ? " //
					+ "IF @valor = '' " //
					+ "    UPDATE DBO.CUSTOM_PARAMETERS SET [VALUE] = ? WHERE ID = ? " //
					+ "commit transaction";

			dc.setSQL(query);
			dc.addParam(syncUsuariosPermisos);
			dc.addParam(hash);
			dc.addParam(syncUsuariosPermisos);

			// Se agrega una demora random entre 0 y la constante definida en
			// milisegundos evitar que ambas
			// instancias tomen el lock de la base de datos en caso de que el
			// nivel de isolation no sea restrictivo
			//Thread.sleep(new Random().nextInt(delay));

			dc.loadResult();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getCustomParam(String parametro) {
		DotConnect dc = new DotConnect();
		String valorParam = "";

		try {

			String query = "select * from custom_parameters where id = ?";

			dc.setSQL(query);
			dc.addParam(parametro);
			List<Map<String, Object>> result = dc.loadObjectResults();

			if (result != null & result.size() > 0) {
				Map<String, Object> resultMap = result.get(0);
				valorParam = (String) resultMap.get("value");
			} else {
				throw new Exception("No existe el parámetro " + parametro + " en la base de datos");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return valorParam;
	}

	public static void unlockSyncUsuariosPermisos() {
		DotConnect dc = new DotConnect();

		try {

			String query = "UPDATE DBO.CUSTOM_PARAMETERS SET [VALUE] = '' WHERE ID = ?";

			dc.setSQL(query);
			dc.addParam(syncUsuariosPermisos);

			dc.loadResult();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static <E> E getField(String query, String field, Class<E> classType, Object... parametros) throws Exception {
		try {
			List<Map<String, Object>> result = executeQuery(query, parametros);

			if (!result.isEmpty()) {
				Map<String, Object> resultMap = result.get(0);
				return (E) Class.forName(classType.getName()).cast(resultMap.get(field));
			}
		} catch (ClassNotFoundException e) {
			Logger.error(CustomDBParametersLoader.class, e.getMessage());
			throw new Exception(e);
		} catch (Exception e) {
			Logger.error(CustomDBParametersLoader.class, e.getMessage());
			throw new Exception(e);
		}

		return null;
	}

	public static List<Map<String, Object>> executeQuery(String query, Object... parametros) throws Exception {
		DotConnect dc = new DotConnect();
		List<Map<String, Object>> result = new ArrayList<>();

		try {

			dc.setSQL(query);
			for (int i = 0; i < parametros.length; i++) {
				dc.addParam(parametros[i]);
			}

			result = dc.loadObjectResults();

		} catch (Exception e) {
			Logger.error(CustomDBParametersLoader.class, e.getMessage());
			throw new Exception(e);
		}

		return result;

	}

}