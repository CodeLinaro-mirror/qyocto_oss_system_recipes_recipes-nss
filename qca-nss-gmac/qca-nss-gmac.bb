DESCRIPTION = "NSS GMAC Driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://ipq806x/LICENSE.txt;md5=b9923979c444c7e3a5a254e01ed5c70a"

inherit module

CLEANBROKEN = "1"

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "file://qca-nss-gmac \
	   file://01-add-clean.patch \
	   "

DEPENDS = "virtual/kernel"

S = "${WORKDIR}/qca-nss-gmac"

PACKAGES += "kernel-module-nss-gmac"
FILES:${PN}-dev = "/usr/include/*"
INSANE_SKIP:${PN} = "dev"

EXTRA_OEMAKE += "TOOL_PATH='${STAGING_BINDIR_TOOLCHAIN}' \
		 SUBDIRS='${S}' \
		SYS_PATH='${STAGING_KERNEL_BUILDDIR}' \
		CROSS_COMPILE='${TARGET_PREFIX}' \
		KVER='${KERNEL_VERSION}' \
		ARCH='arm' \
		"
do_clean() {
	true
}

do_compile() {
	make -C  "${STAGING_KERNEL_BUILDDIR}" \
		CROSS_COMPILE='${TARGET_PREFIX}' \
		ARCH="arm" \
		SUBDIRS="${S}" \
		modules
}

do_install() {
	install -d ${D}/${includedir}/qca-nss-gmac
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 ipq806x/qca-nss-gmac${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install ipq806x/exports/* ${D}/${includedir}/qca-nss-gmac/
}

KERNEL_MODULE_AUTOLOAD += "qca-nss-gmac"
