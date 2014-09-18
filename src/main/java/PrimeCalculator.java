package primeseeker.prime;
import akka.actor.*;
import primeseeker.prime.message.NumberRangeMessage;

public class PrimeCalculator {
	public void calculate(long startNumber, long endNumber) {
		ActorSystem actorSystem = ActorSystem.create("primeCalculator");
		// Create listener
		final ActorRef primeListener = actorSystem.actorOf(new Props(PrimeListener.class), "primeListener");
		ActorRef primeMaster = actorSystem.actorOf(new Props(new UntypedActorFactory() {
			public UntypedActor create() {
				return new PrimeMaster(10, primeListener);
			}
		}), "primeMaster");
		primeMaster.tell(new NumberRangeMessage(startNumber, endNumber));
	}
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Params: java primeseeker <start> <end>");
			System.exit(0);
		}
		long startNumber = Long.parseLong(args[0]);
		long endNumber = Long.parseLong(args[1]);
		PrimeCalculator primeCalculator = new PrimeCalculator();
		primeCalculator.calculate(startNumber, endNumber);
	}
}