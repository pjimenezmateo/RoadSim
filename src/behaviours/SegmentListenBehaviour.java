package behaviours;

import agents.SegmentAgent;
import environment.Segment;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * This behaviour is used by the SegmentAgent and listens to messages
 * either by cars to register, deregister or update themselves from it
 * or from the EventManagerAgent to tell them updates about its status.
 *
 */
public class SegmentListenBehaviour extends Behaviour {

	private static final long serialVersionUID = -2533061568306629976L;

	//Template to listen for the new communications from cars
	private MessageTemplate mtCarControl = 
			MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchOntology("carToSegment"));

	private MessageTemplate mtEventManagerControl = 
			MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchOntology("eventManagerToSegment"));

	private MessageTemplate mt = MessageTemplate.or(mtCarControl, mtEventManagerControl);


	private SegmentAgent agent;

	//Constructor
	public SegmentListenBehaviour(SegmentAgent agent) {

		this.agent = agent;
	}

	@Override
	public void action() {

		ACLMessage msg = myAgent.receive(mt);

		if (msg != null) { //There is a message


			if (msg.getOntology().equals("carToSegment")) {

				String car = msg.getContent();
				String parts[] = car.split("#");

				//Register
				if (msg.getConversationId().equals("register")) { 

					this.agent.addCar(parts[0], Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));

					System.out.println("I'm the car " + parts[0] + " and I'm registering in " + this.agent.getLocalName());

				} else if (msg.getConversationId().equals("deregister")) { //Deregister

					this.agent.removeCar(parts[0]);

					System.out.println("I'm the car " + parts[0] + " and I'm deregistering from " + this.agent.getLocalName());

				} else if (msg.getConversationId().equals("update")) { //Update position

					this.agent.updateCar(parts[0], Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
				}
				
			} else if (msg.getOntology().equals("eventManagerToSegment")) {
				
				Segment segment = this.agent.getSegment();
				
				int density = Integer.parseInt(msg.getContent());
				
				segment.setDensity(density);
			}
			
		} else block();
	}

	@Override
	public boolean done() {

		return false;
	}
}