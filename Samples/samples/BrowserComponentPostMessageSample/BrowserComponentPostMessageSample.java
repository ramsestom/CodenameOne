package com.codename1.samples;


import static com.codename1.ui.CN.*;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Dialog;
import com.codename1.ui.Label;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.codename1.io.Log;
import com.codename1.ui.Toolbar;
import java.io.IOException;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.io.NetworkEvent;
import com.codename1.ui.BrowserComponent;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import com.codename1.ui.TextField;
import com.codename1.ui.layouts.BorderLayout;

/**
 * This file was generated by <a href="https://www.codenameone.com/">Codename One</a> for the purpose 
 * of building native mobile applications using Java.
 */
public class BrowserComponentPostMessageSample {

    private Form current;
    private Resources theme;

    public void init(Object context) {
        // use two network threads instead of one
        updateNetworkThreadCount(2);

        theme = UIManager.initFirstTheme("/theme");

        // Enable Toolbar on all Forms by default
        Toolbar.setGlobalToolbar(true);

        // Pro only feature
        Log.bindCrashProtection(true);

        addNetworkErrorListener(err -> {
            // prevent the event from propagating
            err.consume();
            if(err.getError() != null) {
                Log.e(err.getError());
            }
            Log.sendLogAsync();
            Dialog.show("Connection Error", "There was a networking error in the connection to " + err.getConnectionRequest().getUrl(), "OK", null);
        });        
    }
    
    public void start() {
        if(current != null){
            current.show();
            return;
        }
        Form hi = new Form("Hi World", new BorderLayout());
        BrowserComponent bc = new BrowserComponent();
        
        /* A test page to demonstrate posting messages cross-domain.  The source of this page is:
        <?php
        // This header is really just meant to break the X-Frame-Options sameorigin header
        // that is sent by default by Amazon Lightsail.  
        // In order ot display a page in an iFrame, you may need to fuss with 
        // the X-Frame-Options header.
        // More at https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Frame-Options
        header('X-Frame-Options: allow-from http://localhost:58494');

        ?>
        <!DOCTYPE html>
        <html>
        <head>
        <script
          src="https://code.jquery.com/jquery-3.4.1.min.js"
          integrity="sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo="
          crossorigin="anonymous"></script>

        <script>
        window.addEventListener("message", function(event) {
            var div = jQuery('<div></div>');
            jQuery(div).text(event.data);
            jQuery('body').append(div);
        }, false);

        function postToCN1(msg) {
            if (window.cn1PostMessage) {
                window.cn1PostMessage(msg);
            } else {
                window.parent.postMessage(msg, '*');
            }
        }
        </script>
        </head>
        <body>
        <h1>Listening to Messages...</h1>
        <p><input id='msg' type="text" value="Message To CN1"/></p>
        <p><button onclick="jQuery('body').append(jQuery('<span>foo</span>')); postToCN1(jQuery('#msg').val(), '*')">Send</p>
        </body>
        </html>
        
        */
        bc.setURL("https://weblite.ca/cn1tests/post_message_test.php");
        
        // Register a listener to receive messages sent from the BrowserComponent's webpage.
        // A simple javascript function to send a message to this handler from within your page
        // is:
        // function postToCN1(msg) {
        //    if (window.cn1PostMessage) {
        //        // Case 1: We are running in a native app in a WebView.
        //        window.cn1PostMessage(msg);
        //    } else {
        //        // Case 2: We are running in a Javascript app in an iframe
        //        window.parent.postMessage(msg, '*');
        //    }
        // }

        
        bc.addWebEventListener(BrowserComponent.onMessage, e->{
            CN.callSerially(()->{
                Log.p("Message: "+e.getSource());
                Dialog.show("Here", (String)e.getSource(), "OK", null);
            });
        });
        
        
        TextField message = new TextField("A sample message");
        Button b = new Button("Send Message");
        b.addActionListener(e->{
            
            // Send a message to the webpage.
            // This can be received in the webpage using the 'message' event.
            // E.g.
            /*
            window.addEventListener("message", function(event) {
                var div = jQuery('<div></div>');
                jQuery(div).text(event.data);
                jQuery('body').append(div);
            }, false);
            */
            bc.postMessage(message.getText(), "https://weblite.ca");
                // IMPORTANT: 2nd argument should be either '*' to target any origin
                // or the origin of the web page in the BrowserComponent.
                
            // See https://developer.mozilla.org/en-US/docs/Web/API/Window/postMessage for more information.
        });
        hi.add(CENTER, bc);
        hi.add(BorderLayout.SOUTH, BoxLayout.encloseY(message, b));
        hi.show();
    }

    public void stop() {
        current = getCurrentForm();
        if(current instanceof Dialog) {
            ((Dialog)current).dispose();
            current = getCurrentForm();
        }
    }
    
    public void destroy() {
    }

}
