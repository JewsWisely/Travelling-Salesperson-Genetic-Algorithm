# Travelling Salesperson Problem
https://en.wikipedia.org/wiki/Travelling_salesman_problem
Suppose there is a salesperson who has to travel to a bunch of (n) cities and return back home.
The question is: what is the shortest path he can take to reach all the cities? This is an
NP-hard problem, meaning it is really tough to brute force as you add more and more cities.
To brute force, you'd need to compute all n! routes and compare their distances, which is
possible for low values of n, but it is not scalable.

# Genetic Algorithm
A genetic algorithm can come up with a good solution. For n=100, there are 9.33 * 10^157 routes
to consider, and one of them will be the fastest. Obviously, we can't search through all of
those routes to see which one is fastest, so instead we can use a genetic algorithm to come
up with a "good enough" answer.

If we just create a random route, chances are that it will be pretty bad; lots of travelling
super long distances between cities when it can be easily avoided by just travelling to nearby
ones. The genetic algorithm evolves routes over time to eventually come up with a route that
has no crossings and a relatively short distance. It is possible (actually almost certain) that
the genetic algoirthm will not find the absolute miniumum path between the cities, but in many
cases that isn't necessary; all that's needed is a path that's short enough, and this algorithm
will provide that.