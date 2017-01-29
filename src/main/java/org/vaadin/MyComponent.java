package org.vaadin;

import com.vaadin.shared.MouseEventDetails;
import com.vaadin.ui.AbstractComponent;
import org.vaadin.client.MyComponentClientRpc;
import org.vaadin.client.MyComponentServerRpc;
import org.vaadin.client.MyComponentState;

public class MyComponent extends AbstractComponent {
   
    private int maxClickCount = 5;
    
    private MyComponentServerRpc rpc = new MyComponentServerRpc() {
        private int clickCount = 0;
        
        public void clicked(MouseEventDetails mouseDetails) {
            // nag every 5:th click using RPC
            if (++clickCount % 5 == maxClickCount) {
                getRpcProxy(MyComponentClientRpc.class).alert(
                        "Ok, that's enough!");
            }
            // update shared state
            getState().text = "You have clicked " + clickCount + " times";
        }
    };
    
    public MyComponent() {
        registerRpc(rpc);
        setPrimaryStyleName("mycomponent");
    }
    
    @Override
    public MyComponentState getState() {
        return (MyComponentState) super.getState();
    }

    public int getMaxClickCount() {
        return maxClickCount;
    }

    public void setMaxClickCount(int maxClickCount) {
        this.maxClickCount = maxClickCount;
    }
    
    
}
