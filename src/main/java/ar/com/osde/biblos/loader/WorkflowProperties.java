package ar.com.osde.biblos.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.dotmarketing.util.Logger;

/**
 * @author Emanuel Testa
 *
 */
public class WorkflowProperties {
	private static Properties properties;
	
	public static Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
			InputStream input = null;
			try {
				properties.load(WorkflowProperties.class.getClassLoader().getResourceAsStream("biblos-dotcms-workflow.properties"));
			} catch (IOException e) {
				String msj = "Error al obtener el Properties";
				Logger.error(WorkflowProperties.class, msj, e);
				throw new RuntimeException(msj + " -- " + e.getMessage());
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						String msj = "Error al cerrar InputStream";
						Logger.error(WorkflowProperties.class, msj, e);
						throw new RuntimeException(msj + " -- " + e.getMessage());
					}
				}
			}
		}
		return properties;
	}
}
