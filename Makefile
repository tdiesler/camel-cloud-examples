# Variables
SUBDIRS := \
	main/timer-log \
	quarkus/timer-log \
	spring-boot/http-roll-dice \
	spring-boot/timer-log

package:
	@$(foreach dir, $(SUBDIRS), $(MAKE) -C $(dir) package;)

clean:
	@$(foreach dir, $(SUBDIRS), $(MAKE) -C $(dir) clean;)
