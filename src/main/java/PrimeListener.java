package primeseeker.prime;
import akka.actor.UntypedActor;
import primeseeker.prime.message.Result;

public class PrimeListener extends UntypedActor {
	@Override
	public void onReceive(Object message) throws Exception {
		if (message instanceof Result) {
			Result result = (Result) message;
			System.out.println("Results: ");
			for (Long value: result.getResults()) {
				System.out.print(value + ", ");
			}
			System.out.println();
			// System Shutdown
			getContext().system().shutdown();
		} else {
			unhandled(message);
		}
	}
}