
SUBDIRS := \
    platform-http \
    platform-http-oauth \
    timer-log

package:
	@$(foreach dir, $(SUBDIRS), $(MAKE) -C $(dir) package;)

clean:
	@$(foreach dir, $(SUBDIRS), $(MAKE) -C $(dir) clean;)
