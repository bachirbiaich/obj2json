package main.java.fr.iut.obj2json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Date;

public class Obj2JsonConverter {

    private StringBuilder out = new StringBuilder();
    private int indentLevel = 0;
            
    // ------------------------------------------------------------------------

    public Obj2JsonConverter() {
    }

    // ------------------------------------------------------------------------

    public static String convertObject(Object obj) {
        Obj2JsonConverter conv = new Obj2JsonConverter();
        conv.convertAny(obj);
        return conv.out.toString();
    }

    protected void convertAny(Object obj) {
    	out.append("{\n");
    	convertObjectFields(obj, obj.getClass());
    	out.append("}");
    }

    private void convertObject(Object obj, Class<?> clss) {
    	//On vérifie que l'objet n'est pas null
    	if(obj!=null)
        {
            //Si l'objet est de type tableau
    		if(clss.isArray())
            { 
            	boolean isPrimitiveArray = clss.getComponentType().isPrimitive();
            	if(isPrimitiveArray)
            		convertArrayPrimitive(obj,clss);
            	else
            		convertArray(obj,clss);
            }
    		//Si l'objet est de type primitif
            else if(clss.isPrimitive())
            	convertPrimitive(obj,clss);
    		//Si l'objet est une chaine de caractère
            else if(clss.equals(String.class))
            	out.append("\""+obj+"\"");
    		//Si l'objet est de type Class<?>
            else if(clss.equals(Class.class))
            	out.append("\"Class:"+Class.class.getName()+"\"");
    		//Si l'objet est de type Date
            else if(clss.equals(Date.class))
            	out.append(obj);
    		//S'il s'agit d'un autre objet
            else
            {
            	out.append("{\n");
            	convertObjectFields(obj,clss);
            	printIndent();
            	out.append("}");
            }
        }
    	//Si l'objet est null on affiche null
        else
        	out.append("null");
    }

    private void convertObjectFields(Object obj, Class<?> clss) {
        if (clss != Object.class) {
            Class<?> superClss = clss.getSuperclass();
            // recurse super class fields
            convertObjectFields(obj, superClss);
        }
        indentLevel++;
        Field[] fields = clss.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) 
        {
            Field field = fields[i];
            String name = field.getName();
            printIndent();
            //On affiche le nom de l'attribut
            out.append("\""+name+"\": ");
            //On rend l'attribut accessible pour pouvoir le lire
            field.setAccessible(true);
            
            Object fieldValue=null;
            try 
            {
				//On recupere l'objet dans la variable fieldValue
            	fieldValue = (Object)field.get(obj);
			} 
            catch (IllegalArgumentException | IllegalAccessException e) 
            {
				out.append(e.getMessage());
			}
            //On recupère l'objet dans objField et sa classe dans clssObjField s'il n'est pas nul
            Object objField=fieldValue;
            Class<?> clssObjField=null;
            if(objField!=null)
            	clssObjField=field.getType();
            //On convertit l'objet en format JSON
            convertObject(objField,clssObjField);
            out.append(",\n");
        }
        indentLevel--;
    }

    private void convertPrimitive(Object obj, Class<?> clss) {
    	//S'il s'agit d'un caracatère
    	if(clss.getName().equals("java.lang.Character") || clss.getName().equals("char"))
    		out.append("'"+escapeChar((char)obj)+"'");
    	else
    		out.append(obj.toString());
    }

    private void convertArray(Object obj, Class<?> clss) {
    	out.append("[ \n");
    	indentLevel++;
    	int length = Array.getLength(obj);
    	Object element=null;
    	//On convertit chaque objet contenu dans le tableau d'objets en format JSON
    	for(int j=0;j<length;j++)
    	{
    		element = Array.get(obj, j);
    		Class<?> clssElement=null;
    		if(element!=null)
    			clssElement=element.getClass();
    		printIndent();
    		convertObject(element,clssElement);
    		
    		if(!(j==length-1))
    			out.append(",");
    		out.append("\n");
    	}
    	indentLevel--;
    	printIndent();
    	out.append(" ]");
    	
    }

    private void convertArrayPrimitive(Object obj, Class<?> eltClss) {
    	out.append("[ ");
    	int length = Array.getLength(obj);
    	Object element=null;
    	//On convertit chaque objet primitif contenu dans le tableau en format JSON
    	for(int j=0;j<length;j++)
    	{
    		element = Array.get(obj, j);
    		convertPrimitive(element, element.getClass());
    		if(!(j==length-1))
    			out.append(", ");
    	}
    	out.append(" ]");
   }


   private String escapeChar(char charVal) {
        if (charVal == 0) {
            return "\\0";
        }
        return Character.toString(charVal);
    }

    private void printIndent() {
        for(int i = 0; i < indentLevel; i++) {
            out.append(" ");
        }
    }
    
}
