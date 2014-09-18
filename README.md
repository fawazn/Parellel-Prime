Readme
============================


###Instructions

Make sure you have the [scala build tools] (sbt) installed. If on OSX, you can use brew to install sbt. (brew install sbt)

Then, simply:

```sh
$ sbt 
> compile
> run 1 1000000
```
###Design notes


My submission uses java and the akka actor framework to calculate primes.

A simple way to parallelize the calculation of prime numbers would be to calculate them all independently like so:

```java
	private boolean isPrime(long n) {
		if (n == 1 || n == 2 || n == 3) {
			return true;
		}
		// even?
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
```

This approach (_trial division_) has the advantage that it is 'embarrasingly parallel', i.e., since all the primes are calculated independently of each other it is relatively trivial to parallelize. To do this we divide our prime number N into _t_ different chunks where _t_ is the number of workers or threads, have each worker test all the numbers in the range _t_ independently, and pipe the results back to the master thread.

There are 6 source files included in the project:
> PrimeCalculator.java

> PrimeListener.java

> PrimeMaster.java

> PrimeWorker.java

> NumberRangeMessage.java

> Result.java

The last two are utility classes that define types that are used in the program. 
Execution starts at PrimeCalculator, which contains the main function. It creates a PrimeMaster and PrimeListener -- the latter is only responsible for printing the results once they are obtained; it lies dormant until the result message is recieved.

PrimeCalculator sends a message to PrimeMaster with the start and end numbers from the command line. PrimeMaster then divides the total range into upto 10 workers and invokes PrimeWorkers on each. PrimeWorkers iterate through the range, checks each number for primality, scoops up the ones that are found to be prime, and sends them back to PrimeMaster which passes it on to PrimeListener.

```

                                     ____ PrimeWorker____
                                   / ____ PrimeWorker____ \ 
                                 / / ____ PrimeWorker___ \ \ 
PrimeCalculator --> PrimeMaster-/-/-/---- PrimeWorker---\-\ \ ---> PrimeMaster--->PrimeListener
                                \ \     (<= 10 workers)  /  / 
                                 \_______ PrimeWorker___/  / 
                                  \______ PrimeWorker_____/ 
 .
                                    
```
**Note:**
Using a more efficient algorithm for finding primes like the classic Sieve of Eratosthenes, we can obtain a complexity of Θ(NLogLogN) versus an average of Θ(N^1.5 / (log N)^2) for the primality testing approach detailed above. This would mean much faster results for the tradeoff of increased difficulty to effectively parallelize.

[scala build tools]:http://www.scala-sbt.org/
