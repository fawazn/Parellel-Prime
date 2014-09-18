package primeseeker.prime;
import akka.actor.UntypedActor;
import primeseeker.prime.message.NumberRangeMessage;
import primeseeker.prime.message.Result;

public class PrimeWorker extends UntypedActor {
	/**
	 * Naive primality tester;
	 * Returns true if n is prime, false otherwise
	 *
	 * @param n The number to check
	 ** @return True if n is prime, false if not
	 */
	private boolean isPrime(long n) {
		if (n == 1 || n == 2 || n == 3) {
			return true;
		}
		// Is even?
		if (n % 2 == 0) {
			return false;
		}
		//if not, then just check the odds + 2 till sqrt (n)
		double sqn = Math.sqrt(n);
		for (long i = 3; i <= sqn; i += 2) {
			if (n % i == 0) {
				return false;
			}
		}
		return true;
	}
	//Invoked by the mailbox when it receives a thread timeslice and a message i
	public void onReceive(Object message) {
		if (message instanceof NumberRangeMessage) {
			NumberRangeMessage numberRangeMessage = (NumberRangeMessage) message;
			System.out.println("This worker is responsible for numbers from " + numberRangeMessage.getStartNumber()
			                                    + "to " + numberRangeMessage.getEndNumber());
			// Iterate over the range, compute primes, and return the list of numbers that are prime
			Result result = new Result();
			for (long l = numberRangeMessage.getStartNumber(); l <= numberRangeMessage.getEndNumber(); l++) {
				if (isPrime(l)) {
					result.getResults().add(l);
				}
			}
			getSender().tell(result, getSelf());
		} else {
			unhandled(message);
			//Unexpected circumstance when not a NumberRangeMessage.
		}
	}
}