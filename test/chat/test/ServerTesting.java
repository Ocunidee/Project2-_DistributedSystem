package chat.test;


import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import chat.client.Client;

public class ServerTesting {

	@Test
	public void testConnect() {
	
	    List<Client> clients = new ArrayList<Client>();
	    for(int i = 0; i < 10; i++) {
            clients.add(new Client("127.0.0.1", 4440, false));
        }
	    
	    for(Client client : clients) {
            client.handleCommand("hello");
        }
	    
	    for(Client client : clients) {
            client.handleCommand("#quit");
        }
	    
	    for(Client client : clients) {
            try {
            	client.waitDisconnect();
            	client.disconnect();
			} catch (InterruptedException e) {
			}
        }
	}
	
	@Test
	public void test1() throws Exception {
	
		long sleepDuration = 300;
		
	    Client c1 = new Client("sunrise.cis.unimelb.edu.au", 4442, false);
	    c1.handleCommand("Testing name change");
	    Thread.sleep(sleepDuration);
	    c1.handleCommand("#identitychange proutos");
	    Thread.sleep(sleepDuration);
	    Assert.assertEquals("proutos", c1.getIdentity());
	    
	    Client c2 = new Client("sunrise.cis.unimelb.edu.au", 4442, false);
	    c2.handleCommand("hello");
	    Thread.sleep(sleepDuration);
	    String client2Name = c2.getIdentity();

	    c1.handleCommand("Testing name change that should fail.");
	    Thread.sleep(sleepDuration);
	    c1.handleCommand("#identitychange " + client2Name);
	    Thread.sleep(sleepDuration);
	    Assert.assertEquals("proutos", c1.getIdentity());
	    
	    c1.handleCommand("Testing createroom.");
	    Thread.sleep(sleepDuration);
	    c1.handleCommand("#createroom changeroom");
	    Thread.sleep(sleepDuration);
	    c1.handleCommand("#list");
	    Thread.sleep(sleepDuration);
	    Assert.assertTrue(c1.getRoomsWithGuestCount().containsKey("changeroom"));
	    Assert.assertEquals(0, c1.getRoomsWithGuestCount().get("changeroom").intValue());
	    
	    c1.handleCommand("Testing join.");
	    Thread.sleep(sleepDuration);
	    c1.handleCommand("#join changeroom");
	    Thread.sleep(sleepDuration);
	    Assert.assertEquals("changeroom", c1.getCurrentRoom());
	    
	    c1.handleCommand("Testing who.");
	    Thread.sleep(sleepDuration);
	    c1.handleCommand("#who MainHall");
	    Thread.sleep(sleepDuration);
	    Assert.assertEquals(1, c1.getRoomsContent().get("MainHall").size());
	    Assert.assertEquals("guest1", c1.getRoomsContent().get("MainHall").get(0));
	    
	    c1.handleCommand("#who changeroom");
	    Thread.sleep(sleepDuration);
	    Assert.assertEquals(1, c1.getRoomsContent().get("changeroom").size());
	    Assert.assertEquals("proutos", c1.getRoomsContent().get("changeroom").get(0));
	    
	    c1.handleCommand("#createroom deleteTest");
	    Thread.sleep(sleepDuration);
	    c2.handleCommand("#join deleteTest");
	    Thread.sleep(sleepDuration);
	    Assert.assertEquals("deleteTest", c2.getCurrentRoom());
	    
	    c1.handleCommand("Testing delete fail.");
	    Thread.sleep(sleepDuration);
	    c2.handleCommand("#delete deleteTest");
	    Thread.sleep(sleepDuration);
	    c2.handleCommand("#list");
	    Thread.sleep(sleepDuration);
	    Assert.assertTrue(c2.getRoomsWithGuestCount().containsKey("deleteTest"));
	    c1.handleCommand("Testing delete.");
	    Thread.sleep(sleepDuration);
	    c1.handleCommand("#delete deleteTest");
	    Thread.sleep(sleepDuration);
	    Assert.assertEquals("MainHall", c2.getCurrentRoom());
	    Assert.assertTrue(!c1.getRoomsWithGuestCount().containsKey("deleteTest"));
	    
	    c2.handleCommand("#join changeroom");
	    Thread.sleep(sleepDuration);
	    c1.handleCommand("Testing kick.");
	    Thread.sleep(sleepDuration);
	    c1.handleCommand("#kick changeroom guest1 10");
	    Thread.sleep(sleepDuration);
	    Assert.assertEquals("MainHall", c2.getCurrentRoom());
	    c1.handleCommand("Testing join fail just after kick.");
	    Thread.sleep(sleepDuration);
	    c2.handleCommand("#join changeroom");
	    Thread.sleep(sleepDuration);
	    Assert.assertEquals("MainHall", c2.getCurrentRoom());
	    Thread.sleep(10000);
	    c1.handleCommand("Testing join success after ban time elapsed.");
	    Thread.sleep(sleepDuration);
	    c2.handleCommand("#join changeroom");
	    Thread.sleep(sleepDuration);
	    Assert.assertEquals("changeroom", c2.getCurrentRoom());
	    
	    c1.handleCommand("#quit");
	    c1.waitDisconnect();
	    c1.disconnect();
	    
	    c2.handleCommand("#list");
	    Thread.sleep(sleepDuration);
	    Assert.assertTrue(c2.getRoomsWithGuestCount().containsKey("changeroom"));
	    
	    c2.handleCommand("Testing auto deletion on last person leaving.");
	    Thread.sleep(sleepDuration);
	    c2.handleCommand("#join MainHall");
	    Thread.sleep(sleepDuration);
	    c2.handleCommand("#list");
	    Thread.sleep(sleepDuration);
	    Assert.assertTrue(!c2.getRoomsWithGuestCount().containsKey("changeroom"));
	    
	    c2.handleCommand("#quit");
	    c2.waitDisconnect();
	    c2.disconnect();
	}
}
