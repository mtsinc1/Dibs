# CodeRanch Dibs utility.

## Explanation

The CodeRanch/JavaRanch Big Moose Saloon is an Internet message
board designed to be "A Friendly Place for Programming Greenhorns".
It has a large collection of forums dedicated to different topics
in programming, Information Technology and occasional Meaningless
Drivel.

Each Forum has (ideally) at least one Moderator (a/k/a "Bartender")
who is a member of the CodeRanch Staff. Several times a year, the
staff get together online and vote on new recruits. Once the
potential staff members have been approved and have accepted the
position, the forums are thrown open for bidding. The bidding
process allows staff members to call "dibs" on the forum(s) that
they wish to moderate. Once the dibs window has closed, the new
and updated moderations are posted to the forum database.

Traditionally, the method of calling dibs has been not unlike how
my classmates liked to play the "Star Trek" game when I was in
college and we shared a mini-computer. As each Star Date advanced,
a whole new quadrant map would print out on the Teletype console.
The CodeRanch equivalent uses a message thread, but it's the
same concept, only with less technology.

The Dibs webapp described herein is an attempt to make the process
a little more immediate. Instead of redrawn maps, you get an
up-to-date web display and a GUI shuttle control that allows you
to select and prioritize your dibs bids.

If/when time permits, I'll include a formal Functional Spec document
for this project. Since it was primarily an R&D sandbox project it
didn't have formal specs defined in advance.

## Roles

This webapp allows 2 types of user roles.

1. The Dibs Administrator has the ability to completely wipe the
Dibs database and to backup and restore (merge) using YAML data
dump files.

1. The Dibs User has the ability to place or change Dibs bids
using a GUI shuttle control. Bids are prioritized, so the top bid
on the list is the preferred forum if not all forums are available.

## Constraints

1. The actual Dibs-to-moderation conversion should prioritize
such that newcomers get first crack at a forum, thereafter bidders
should be applied by the priority they assigned.

1. Each Forum has an assigned number of moderators. If more people
bid on a forum than there are available moderator slots, assignment
is done according to the rules previously described and extra bids
are ignored.

## TO DO

In its current version (2015-06-01), this app cannot actually
do the moderations assignments automatically. Also there is
no security in place at present. I do security via the J2EE
standard container security system, so that's something that
can be wrapped around the finished app rather than coded in.

## Technology

Although this app servers a very real and practical purpose, its
primary reason for existence was to work with the Neo4j database.
Since Neo4j is primarily about relationships and that's what Dibs
and moderations are, it's a good fit.

I get my GUI boost from JavaServer Faces v2.2 and RichFaces 4.
I'm using Spring for my persistence infrastructure, specifically
Spring Data for Neo4j or "SDN" as it's known for short.

## Apologies

Both Neo4j and SDN went through some significant changes over the
(extended) course of development of this application. I've tried
to use best practices, allowing for moving targets, but have not
been completely successful. In particular, the automated
transaction management that I'm accustomed to seeing in Spring
seems to be incompletely applied in the current release and the
only solutions that I've seen promoted involved manual transaction
management. So that's what I've done, but I hope to revisit
with a cleaner solution once one becomes apparent.

## Installation and Deployment

This webapp builds (via Maven) to product a deployable WAR file.
It uses an embedded Neo4j database, whose physical disk location
is configured as a deployment option. For Linux and Tomcat, the
recommended file location is /var/lib/neo4j and the "Dibs.xml"
file in this project is set up for that. Please note that the
database directory must be writeable by whatever user the webapp
is running under.
