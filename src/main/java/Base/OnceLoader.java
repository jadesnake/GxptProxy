package Base;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class OnceLoader implements ApplicationListener<ContextRefreshedEvent> {
    private boolean loaded = false;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event){
        if (loaded) {
            return;
        }
        load(event.getApplicationContext());
        loaded = true;
    }
    protected void load(ApplicationContext applicationContext) {

    }
}
