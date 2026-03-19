The code is written in kotlin. It works by first running BFS from the starting station to discover which stations are reachable from it. Then it runs BFS from each reachable station and adds that stations cargo type to every other station it reaches. If during BFS a station is reached that unloads the original stations cargo type, BFS does not recurse further as the cargo would be unloaded here.

The result is outputted as: station index: list of cargo types that might be on a train when it arrives
