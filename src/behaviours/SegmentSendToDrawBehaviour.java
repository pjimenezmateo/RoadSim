package behaviours;

import agents.SegmentAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SegmentSendToDrawBehaviour extends CyclicBehaviour {

	private static final long serialVersionUID = -6962886464372429902L;

	private SegmentAgent agent;
	private DFAgentDescription interfaceAgent;

	private MessageTemplate mtTick = 
			MessageTemplate.and(
					MessageTemplate.MatchPerformative(ACLMessage.INFORM),
					MessageTemplate.MatchConversationId("tick"));

	public SegmentSendToDrawBehaviour(SegmentAgent agent) {

		this.agent = agent;

		//Find the interface agent
		DFAgentDescription dfd = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("interface");
		dfd.addServices(sd);

		DFAgentDescription[] result = null;

		try {
			result = DFService.searchUntilFound(
					this.agent, this.agent.getDefaultDF(), dfd, null, 5000L);
		} catch (FIPAException e) { e.printStackTrace(); }

		this.interfaceAgent = result[0];

	}

	@Override
	public void action() {

		//Block until tick is received
		ACLMessage msg = myAgent.receive(mtTick);

		if (msg != null) {

			if (this.agent.carsSize() > 0) {
				
				//Send the data to the InterfaceAgent
				msg = new ACLMessage(ACLMessage.INFORM);
				msg.setOntology("drawOntology");
				msg.addReceiver(this.interfaceAgent.getName());
				msg.setContent(this.agent.getDrawingInformation());

				myAgent.send(msg);
			}
		} else block();
	}
}