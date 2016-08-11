package com.nirvana.menu;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang.ClassUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class NMSManager {

	@SuppressWarnings("unchecked")
	
	public static <T> T getValueFromEntityHandle(Entity entity, String fieldName) {

		try {
			Object obj = getHandle(entity);

			Field field = obj.getClass().getDeclaredField(fieldName);

			return (T) field.get(obj);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	
	public static <T> T invokeMethodForEntityHandle(Entity entity, String string) {
		try {
			Object obj = getHandle(entity);
			Method method2 = null;
			
			try{
				method2 = obj.getClass().getDeclaredMethod(string);
			}catch(NoSuchMethodException e){
				method2 = obj.getClass().getMethod(string);
			}

			return (T) method2.invoke(obj);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void addEntityTarget(Entity entity, int id, String className,
			Object... params) {
		try {

			Class<?> goalClass = Class
					.forName(getPackage() + ".PathfinderGoal");

			Method method = entity.getClass().getDeclaredMethod("getHandle");

			Object nmsEntity = method.invoke(entity);

			Field goalSelectorField = nmsEntity.getClass().getField(
					"targetSelector");

			Object goals = goalSelectorField.get(nmsEntity);

			Method putMethod = goals.getClass().getDeclaredMethod("a",
					int.class, goalClass);

			Class<?> toAddClass = Class.forName(getPackage() + "." + className);

			Constructor<?> constructor = getConstructor(toAddClass, params);
			
			if(constructor == null){
				throw new NoSuchMethodException("No constructor found!");
			}

			Object newGoal = constructor.newInstance(params);

			putMethod.invoke(goals, id, newGoal);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	public static void addEntityGoal(Entity entity, int id, String className,
			Object... params) {
		try {

			Class<?> goalClass = Class
					.forName(getPackage() + ".PathfinderGoal");

			Method method = entity.getClass().getDeclaredMethod("getHandle");

			Object nmsEntity = method.invoke(entity);

			Field goalSelectorField = nmsEntity.getClass().getField(
					"goalSelector");

			Object goals = goalSelectorField.get(nmsEntity);

			Method putMethod = goals.getClass().getDeclaredMethod("a",
					int.class, goalClass);

			Class<?> toAddClass = Class.forName(getPackage() + "." + className);
			
			Constructor<?> constructor = null;

			constructor = getConstructor(toAddClass, params);

			if(constructor == null){
				throw new NoSuchMethodException("No constructor found!");
			}
			
			Object newGoal = constructor.newInstance(params);

			putMethod.invoke(goals, id, newGoal);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	public static void clearEntityAi(Entity entity) {
		try {
			Method method = entity.getClass().getDeclaredMethod("getHandle");

			Object nmsEntity = method.invoke(entity);

			Object emptyGoals = getEmptyPathfinderGoalSelector(entity
					.getWorld());

			Field goalSelector = nmsEntity.getClass().getField("goalSelector");
			Field targetSelector = nmsEntity.getClass().getField(
					"targetSelector");

			goalSelector.set(nmsEntity, emptyGoals);
			targetSelector.set(nmsEntity, emptyGoals);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Object getWorldServer(World world) {

		try {
			Method method = world.getClass().getDeclaredMethod("getHandle");

			return method.invoke(world);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	private static Object getEmptyPathfinderGoalSelector(World world) {

		try {
			Class<?> pathClass = Class.forName(getPackage()
					+ ".PathfinderGoalSelector");

			Class<?> profilerClass = Class.forName(getPackage()
					+ ".MethodProfiler");

			Constructor<?> con = pathClass.getConstructor(profilerClass);

			Object worldServer = getWorldServer(world);

			Field profilerField = worldServer.getClass().getField(
					"methodProfiler");

			return con.newInstance(profilerField.get(worldServer));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	private static String getPackage() {
		String serverPackage = Bukkit.getServer().getClass().getPackage()
				.getName();

		return "net.minecraft.server."
				+ serverPackage.substring(serverPackage.lastIndexOf('.') + 1);
	}

	
	public static Object getHandle(Entity entity) {

		Object obj = null;
		try {
			Method method = entity.getClass().getDeclaredMethod("getHandle");

			obj = method.invoke(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return obj;
	}

	
	public static Class<?> getClass(String name) {
		try {
			return Class.forName(getPackage() + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	public static void setValue(Object obj, String fieldName, Object value)
	{
		try{
			obj.getClass().getField(fieldName).set(obj, value);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T invokeMethod(Object obj, String methodName, Object... params)
	{
		
		try{
			T returnObject = (T) obj.getClass().getMethod(methodName, getClassArray(params)).invoke(obj, params);
			return returnObject;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static Class<?>[] getClassArray(Object... params){
		Class<?>[] clazzes = new Class<?>[params.length];
		
		for(int i = 0; i < params.length;i++){
			
			Class<?> clazz = params[i].getClass();
			
			if(ClassUtils.wrapperToPrimitive(clazz) != null){
				clazz = ClassUtils.wrapperToPrimitive(clazz);
			}
			
			clazzes[i] = clazz;
			
		}
		
		return clazzes;
	}
	
	private static Constructor<?> getConstructor(Class<?> clazz, Object... params){
		
		Constructor<?> constructor = null;
		
		for (Constructor<?> c : clazz.getConstructors()) {
			Class<?>[] stuff = c.getParameterTypes();

			if (stuff.length != params.length) {
				continue;
			}

			boolean is = true;

			for (int i = 0; i < stuff.length; i++) {
				Class<?> c1 = stuff[i];

				c1 = ClassUtils.primitiveToWrapper(c1);

				Object o = params[i];
				
				if (!c1.isInstance(o)) {
					is = false;
					break;
				}
			}

			if (is) {
				constructor = c;
				break;
			}

		}
		
		return constructor;
	}

}
