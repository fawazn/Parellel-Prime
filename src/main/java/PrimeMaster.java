package primeseeker.prime;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.routing.RoundRobinRouter;
import primeseeker.prime.message.NumberRangeMessage;
import primeseeker.prime.message.Result;
import java.util.List;


public class PrimeMaster extends UntypedActor {
	private final ActorRef workerRouter;
	private final ActorRef listener;
	private final int numberOfWorkers;
	private int numberOfResults = 0;
	private Result finalResults = new Result();

	public PrimeMaster(final int numberOfWorkers, ActorRef listener) {
		this.numberOfWorkers = numberOfWorkers;
		this.listener = listener;
		workerRouter = this.getContext()
			.actorOf(new Props(PrimeWorker.class)
			.withRouter(new RoundRobinRouter(numberOfWorkers)), "workerRouter");
	}

	@Override
	public void onReceive(Object message) {
		if (message instanceof NumberRangeMessage) { //Would be a great place to use case classes and pattern matching!
			NumberRangeMessage numberRangeMessage = (NumberRangeMessage) message;
			// Always broken into 10 workers
			long numberOfNumbers = numberRangeMessage.getEndNumber() - numberRangeMessage.getStartNumber();
			long segmentlength = numberOfNumbers / 10;
			for (int i = 0; i < numberOfWorkers; i++) {
				// Compute the start and end numbers for this worker; Handle remainders
				long startNumber = numberRangeMessage.getStartNumber() + (i * segmentlength);
				long endNumber = startNumber + segmentlength - 1;
				if (i == numberOfWorkers - 1) {
					endNumber = numberRangeMessage.getEndNumber();
				}
				// Send a message to the work router to kick off prime checking for this range of numbers
				workerRouter.tell(new NumberRangeMessage(startNumber, endNumber), getSelf());
			}
		} else if (message instanceof Result) {
			//Result message recieved; append them to the final results
			Result result = (Result) message;
			finalResults.getResults().addAll(result.getResults());
			if (++numberOfResults >= 10) {
				listener.tell(finalResults, getSelf());
				getContext().stop(getSelf());
			}
		} else {
			unhandled(message);
		}
	}
}