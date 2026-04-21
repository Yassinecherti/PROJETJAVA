package projet;
import java.util.concurrent.atomic.*;

public class Id  {
	 
	   private static final AtomicInteger ID_FACTORY = new AtomicInteger(1000);
	
	   private final int  id = ID_FACTORY.getAndIncrement();
	    public final int getId() {
	      return id;
	   }

}
