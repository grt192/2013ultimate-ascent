package event.events;


/**
 *
 * @author keshav
 */
public class MacroEvent {

    private String source = "";
    
    public MacroEvent(String source){
        this.source = source;
    }
    
    public String getSource(){
        return source;
    }
    
}
