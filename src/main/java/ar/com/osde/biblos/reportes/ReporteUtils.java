package ar.com.osde.biblos.reportes;

import java.lang.reflect.Field;
import java.sql.Date;

import com.dotmarketing.util.Logger;

public class ReporteUtils {

	public static void fixNullAttributes(Object entity) {
		   Class<?> clazz = entity.getClass();
		
		   for(Field field : clazz.getDeclaredFields()) {
			   field.setAccessible(true);
			   Object value = null;
			   try {
				   value = field.get(entity);

				   if(value == null){
					   Class<?> type = field.getType();
					   if(String.class.isAssignableFrom(type)){
						   field.set(entity, "");
					   } else if(Boolean.class.isAssignableFrom(type)){
						   field.set(entity, false);
					   } else continue;
					   
					   Logger.error(ReporteUtils.class, "El campo " + field.getName() 
					   		+ " no puede ser null para la entidad: " + entity.getClass().getSimpleName() + ".");
				   }
				   
			   } catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
			   }
		   }
	}
}
