# Variables
SUBDIRS := main/timer-log quarkus/timer-log spring-boot/http-roll-dice spring-boot/timer-log

package: $(SUBDIRS)
	@$(foreach dir, $(SUBDIRS), $(MAKE) -C $(dir) package;)

clean: $(SUBDIRS)
	@$(foreach dir, $(SUBDIRS), $(MAKE) -C $(dir) clean;)
