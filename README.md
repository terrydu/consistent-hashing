# Consistent-Hashing
Demo of consistent hashing.

# Problem
A "sticky" or persistent load balancer aims to send a client (for example a person) to
the same back-end server. This is typically done by hashing some client input and then
doing a modulo operation on it with the number of back-end servers.

For example let's say "Jane" is trying to access a service with a sticky load balancer 
that is in front of 4 back-end servers.  The client details (ex.: source IP, SSL 
Session Id, etc...) are hashed together and we perform a modulo operation on it. If we
perform this with enough clients we'll get a roughly equal distribution. Perhaps like
the following:

    Hash("Jane") % 4 = Server2
    Hash("Alan") % 4 = Server1
    Hash("Lima") % 4 = Server3

What happens if a server goes down?  That means we now do a modulo 3
operation. Since these are hashes, that means that just about all clients will now
resolve to a different back-end server.  We might end up with something like this:

    Hash("Jane") % 3 = Server3
    Hash("Alan") % 3 = Server2
    Hash("Lima") % 3 = Server1

# Goal

What we'd really like is that if a server goes down then only the clients that
were resolving to that server get rebalanced across the remaining healthy servers.
In the example above if `Server3` goes down then we'd like Lima to go to a new server,
but we'd still like Jane and Alan to go to the same servers that we had before.

    Hash("Jane") % 4 = Server2
    Hash("Alan") % 4 = Server1
    Hash("Lima") % 4 = Server3 -> Server1

# Solution

The way to achieve this is to imagine that we have a large circle (or a line that 
loops), with the servers placed more or less spread out evenly on the circle.  In
fact, for better distribution in case of fail-over we have multiple instances of
each server located on this circle.  If we envision this as a line that loops, with
2 "instances" of each server, it might look something like this:

     1 | 
     2 | Server1
     3 | 
     4 | Server2
     5 | 
     6 | Server3
     7 | 
     8 | Server1
     9 | 
    10 | Server2
    11 | 
    12 | Server3

If a server goes down then we remove all instances of that server from our looped
line... but the other server instances stay at their existing location.  So for
example if Server3 goes down then it might look like this:

     1 | 
     2 | Server1
     3 | 
     4 | Server2
     5 | 
     6 |
     7 | 
     8 | Server1
     9 | 
    10 | Server2
    11 | 
    12 | 

If a server gets added then we find the biggest gap(s) in our looped line and add
the server instances there.  So for example if we add `Server4` with 2 instances 
then it would be located as follows:

     1 | 
     2 | Server1
     3 | 
     4 | Server2
     5 | 
     6 | Server4 (new!)
     7 | 
     8 | Server1
     9 | 
    10 | Server2
    11 | 
    12 | Server4 (new!)

When an incoming connection from a client comes in we map it to this circle (or
line in our case) and then find the server instance that is next along the line.
So for example if `Jane` wishes to connect she might result in a hash that gets
placed at location 9 on our line:

    Client | Index | Server
           |   1   | 
           |   2   | Server1
           |   3   | 
           |   4   | Server2
           |   5   | 
           |   6   | Server4
           |   7   | 
           |   8   | Server1
      Jane |   9   | 
           |  10   | Server2
           |  11   | 
           |  12   | Server4

The next server on our line would be Server 2.

Two things to note here:
* In the future if Server1 or Server4 goes down, Jane will still go to `Server2`. That's exactly what we want!
* In the future if Server2 goes down, then Jane will failover to the next server on the line, which would be `Server4` in the example above.

In practice a server might have 100s of instances, as it more evenly distributes the affected
clients across the remaining back-end servers.

As an additional benefit, if all of our back-end servers are not equal in terms of what
load they can handle then we give them different weights (or instance counts).

# Try it Yourself

Currently only a Java command-line interface (CLI) application has an implementation (my own)
of consistent hashing so that you can try it yourself. Normally you'd try something like this:

1. Optionally: Add some more clients (people) and servers
2. Start removing servers, and confirm that most clients are unaffected.
