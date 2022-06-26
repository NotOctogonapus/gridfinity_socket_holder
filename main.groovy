// Generates a socket holder for the given list of sockets.
def generateSocketHolder(
	sockets,
	socketRotationDeg = 50,
	gapBetweenSockets = 1.5,
	offsetTowardsPlateCenterX = 5,
	holderHeightMM = 20
) {
	File plateUnitFile = ScriptingEngine.fileFromGit("https://github.com/NotOctogonapus/gridfinity_socket_holder.git", "isolated box plate v1.stl")
	def plateUnit  = Vitamins.get(plateUnitFile)
	plateUnit = plateUnit.movez(-plateUnit.getMaxZ())

	def newSockets = sockets.collect { it }
	newSockets.eachWithIndex { it, i ->
//		def cutter = CSG.unionAll([it, it.movey(20/Math.tan(Math.toRadians(-socketRotationDeg))).movez(20)])

//		def polys = Slice.slice(it, new Transform().rotx(socketRotationDeg).movey(20/Math.tan(Math.toRadians(-socketRotationDeg))).movez(20), 0)
//		def innerCross = polys[0]
//		def crossExtrusion = Extrude.polygons(innerCross, 20)
//		cutter = crossExtrusion

//		def slicer = new Cube(100, 100, 0.01).toCSG().rotx(socketRotationDeg+90)
//		def slice = it.intersect(slicer)
//		def movedSlice = slice.movey(20/Math.tan(Math.toRadians(-socketRotationDeg))).movez(20)
//		def cutter = CSG.hullAll([slice, movedSlice])

		def poly = Slice.slice(it,new Transform().rotx(90),0)[0]
		def cutter = Extrude.polygons(poly, 20)
		cutter = cutter.toolOffset(1)
		cutter = cutter.toYMin().rotx(90+socketRotationDeg).toYMin().toZMin()
//		newSockets[i] = cutter
//		return
		//cutter = cutter.rotx(90+socketRotationDeg)
//		cutter = it

		if (i == 0) {
			newSockets[i] = cutter.movex(-newSockets[i].getMinX()).movex(offsetTowardsPlateCenterX)
		}
		else {
			newSockets[i] = cutter.movex(newSockets[i-1].getMaxX() + it.getTotalX()/2 + gapBetweenSockets)
		}

		def offsetTowardsPlateCenterY = plateUnit.getMaxY() - cutter.getMaxY() - plateUnit.getTotalY()/2 + cutter.getTotalY()/2
		newSockets[i] = newSockets[i]
			.movey(offsetTowardsPlateCenterY)
			.movez(-newSockets[i].getMinZ())
	}

//	def socketsCutterParts = newSockets.collect {
//		CSG.unionAll(
//			[it,
//			it.movez(20)
//				.movey((20)/Math.tan(Math.toRadians(90+socketRotationDeg)))]
//		)
//	}
	//def socketsCutterParts = newSockets.collect { it } // .toolOffset(0.5)
	def socketsCutter = CSG.unionAll(newSockets)
	
	def numPlatesNeeded = (int) Math.ceil(socketsCutter.getTotalX() / plateUnit.getTotalX())
	def plates = (1..numPlatesNeeded).collect { it -> plateUnit.movex(plateUnit.getTotalX() * (it-1)) }
	def plateUnion = CSG.unionAll(plates)

	// TODO: bevel the edges of this cube which are parallel to the Z-axis
	def socketsVolume = new Cube(plateUnion.getTotalX(), plateUnion.getTotalY(), holderHeightMM).toCSG()
	socketsVolume = socketsVolume.movex(-socketsVolume.getMinX()).movey(-socketsVolume.getMinY()).movez(-socketsVolume.getMinZ())
	socketsVolume = socketsVolume.difference(socketsCutter)

	return CSG.unionAll([plateUnion, socketsVolume])
//	return [plateUnion, socketsVolume] //, CSG.unionAll(newSockets)
//	return socketsCutter
}

return generateSocketHolder(
	[
		Vitamins.get("socket", "6mm"),
		Vitamins.get("socket", "12mm"),
		Vitamins.get("socket", "19mm"),
	]
)
