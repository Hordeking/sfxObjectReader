default: starfox/Main.java starfox/struc/SFXObjectId.java starfox/struc/SFXObjectHeader.java starfox/struc/Face.java starfox/struc/FaceGroup.java starfox/struc/BSPTree.java starfox/struc/SFXObject.java util/BufferReader.java math/Vertex.java math/Normal.java math/Triangle.java
	javac */*.java

.PHONY: clean
clean:
	find -iname "*.class" -delete
