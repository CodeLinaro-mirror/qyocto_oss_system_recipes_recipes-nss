#!/bin/sh
#
# Copyright (c) 2019, The Linux Foundation. All rights reserved.
#
# Permission to use, copy, modify, and/or distribute this software for any
# purpose with or without fee is hereby granted, provided that the above
# copyright notice and this permission notice appear in all copies.
#
# THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
# WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
# MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
# ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
# WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
# ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
# OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
#

#
# This script will be executed as and when the contents of
# /lib/firmware directory are modified.
#

if [ -e /lib/firmware/qca-nss0-enterprise.bin ] ; then
	rm -f /lib/firmware/qca-nss0.bin
	ln -s /lib/firmware/qca-nss0-enterprise.bin /lib/firmware/qca-nss0.bin
elif [ -e /lib/firmware/qca-nss0-retail.bin ] ; then
	rm -f /lib/firmware/qca-nss0.bin
	ln -s /lib/firmware/qca-nss0-retail.bin /lib/firmware/qca-nss0.bin
fi

if [ -e /lib/firmware/qca-nss1-enterprise.bin ] ; then
	rm -f /lib/firmware/qca-nss1.bin
	ln -s /lib/firmware/qca-nss1-enterprise.bin /lib/firmware/qca-nss1.bin
elif [ -e /lib/firmware/qca-nss1-retail.bin ] ; then
	rm -f /lib/firmware/qca-nss1.bin
	ln -s /lib/firmware/qca-nss1-retail.bin /lib/firmware/qca-nss1.bin
fi
