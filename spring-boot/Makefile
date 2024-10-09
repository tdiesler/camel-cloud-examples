
SUBDIRS := \
    http-roll-dice \
    timer-log

package:
	@$(foreach dir, $(SUBDIRS), $(MAKE) -C $(dir) package;)

clean:
	@$(foreach dir, $(SUBDIRS), $(MAKE) -C $(dir) clean;)
