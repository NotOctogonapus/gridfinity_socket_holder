// Generates a socket holder for the given list of sockets.
def generate_socket_holder(
	sockets,
	socket_rotation_deg = 50,
	gap_between_sockets = 1.5,
	offset_towards_plate_center_x = 5,
	offset_towards_plate_center_y = 5
) {
	File plateUnitFile = ScriptingEngine.fileFromGit("https://github.com/NotOctogonapus/gridfinity_socket_holder.git", "isolated box plate v1.stl")
	def plateUnit  = Vitamins.get(plateUnitFile)
	plateUnit = plateUnit.movez(-plateUnit.getMaxZ())

	def new_sockets = sockets.collect { it.rotx(socket_rotation_deg) }
	new_sockets.eachWithIndex { it, i ->
		if (i == 0) {
			new_sockets[i] = new_sockets[i].movex(-new_sockets[i].getMinX())
		}
		else {
			new_sockets[i] = it.movex(new_sockets[i-1].getMaxX() + it.getTotalX()/2 + gap_between_sockets)
		}

		new_sockets[i] = new_sockets[i].movex(offset_towards_plate_center_x)
			.movey(offset_towards_plate_center_y)
			.movez(-new_sockets[i].getMinZ())
	}

	def sockets_union = CSG.unionAll(new_sockets)
	def num_plates_needed = sockets_union.getTotalX() / plateUnit.getTotalX()
	println num_plates_needed
	
	return sockets_union
}

return generate_socket_holder(
	[
		Vitamins.get("socket", "6mm"),
		Vitamins.get("socket", "12mm"),
		Vitamins.get("socket", "19mm"),
	]
)
