#This is a configuration file for the Epidemic Spreading Project. It takes the input in the form of key-value pairs

#number of nodes in the graph#10 
num_nodes = 50

#number of edges of the graph#1225
num_edges = 1225

#time units for the simulation
time_units = 60

#number of people or total population
num_people = 100000

#number of HCUs
num_HCUs = 3

#number of people initially infected (some fraction of people)
num_infected = 4000

#connectivity between the people either BA or Random; 1-Random and 2-BA
network_topology = 2

#this parameter for random topology(not for BA)
topology_density  = 0.1 

#fractions of people presented in each city or distribution of people among the cities
#population_dist = [0.10269572, 0.091075994, 0.06969163, 0.057720345, 0.046038687, 0.03835277, 0.0371144, 0.036875892, 0.025788365, 0.02514214, 0.023251563, 0.022824375, 0.019855658, 0.016803905, 0.016182477, 0.015012438, 0.014841966, 0.014273625, 0.013892641, 0.01375648, 0.013503628, 0.013320478, 0.013217802, 0.013087938, 0.012885103, 0.0122730415, 0.011593596, 0.010804294, 0.010622481, 0.010287265, 0.0100797005, 0.00991943, 0.009844957, 0.009667816, 0.009587185, 0.009349478, 0.009239837, 0.009220168, 0.008859861, 0.008849304, 0.008702181, 0.008695323, 0.008537289, 0.0085336575, 0.008336963, 0.0082676895, 0.007951862, 0.007930055, 0.00785025, 0.00779032]

population_dist = [0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02, 0.02]

#population_dist = [0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1]
