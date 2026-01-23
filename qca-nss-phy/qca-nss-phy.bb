DESCRIPTION = "NSS PHY Driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

FILESPATH = "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "file://qca-nss-phy"

DEPENDS = "virtual/kernel"

S = "${WORKDIR}/qca-nss-phy"

EXTRA_CFLAGS +="-I${STAGING_INCDIR}"

SUPPORTED_PHYS_DEFAULT = "CONFIG_NSSPHY_QCA808X CONFIG_NSSPHY_QCA81XX CONFIG_NSSPHY_QCA803X CONFIG_NSSPHY_QCE1204"
QCA_NSS_PHY_SUPPORTED_PHYS ?= "${SUPPORTED_PHYS_DEFAULT}"

# ipq95xx / ipq96xx add QCA807X
QCA_NSS_PHY_SUPPORTED_PHYS:ipq95xx     = "${SUPPORTED_PHYS_DEFAULT} CONFIG_NSSPHY_QCA807X"
QCA_NSS_PHY_SUPPORTED_PHYS:ipq95xx_64  = "${SUPPORTED_PHYS_DEFAULT} CONFIG_NSSPHY_QCA807X"
QCA_NSS_PHY_SUPPORTED_PHYS:ipq96xx     = "${SUPPORTED_PHYS_DEFAULT} CONFIG_NSSPHY_QCA807X"
QCA_NSS_PHY_SUPPORTED_PHYS:ipq96xx_64  = "${SUPPORTED_PHYS_DEFAULT} CONFIG_NSSPHY_QCA807X"

# ipq52xx adds QCA807X + IPQ52XX
QCA_NSS_PHY_SUPPORTED_PHYS:ipq52xx     = "${SUPPORTED_PHYS_DEFAULT} CONFIG_NSSPHY_QCA807X CONFIG_NSSPHY_IPQ52XX"
QCA_NSS_PHY_SUPPORTED_PHYS:ipq52xx_64  = "${SUPPORTED_PHYS_DEFAULT} CONFIG_NSSPHY_QCA807X CONFIG_NSSPHY_IPQ52XX"

# ipq53xx/ipq54xx keep default
QCA_NSS_PHY_SUPPORTED_PHYS:ipq53xx     = "${SUPPORTED_PHYS_DEFAULT}"
QCA_NSS_PHY_SUPPORTED_PHYS:ipq53xx_64  = "${SUPPORTED_PHYS_DEFAULT}"
QCA_NSS_PHY_SUPPORTED_PHYS:ipq54xx     = "${SUPPORTED_PHYS_DEFAULT}"
QCA_NSS_PHY_SUPPORTED_PHYS:ipq54xx_64  = "${SUPPORTED_PHYS_DEFAULT}"

QCA_NSS_PHY_CONFIG_OPTS+= "TOOL_PATH=${STAGING_BINDIR_TOOLCHAIN} \
                         SYS_PATH=${STAGING_KERNEL_BUILDDIR} \
                         TOOLPREFIX=${TARGET_PREFIX} \
                         KVER=${KERNEL_VERSION} \
			 EXTRA_CFLAGS="${EXTRA_CFLAGS}" \
                         ARCH=${KARCH} \
                         OS='linux' "

# enable PTP unless CONFIG_KERNEL_IPQ_MEM_PROFILE=256 or CONFIG_LOWMEM_FLASH=y
QCA_NSS_PHY_CONFIG_OPTS:append = "${@' ' if d.getVar('CONFIG_KERNEL_IPQ_MEM_PROFILE', True) == '256' or d.getVar('CONFIG_LOWMEM_FLASH', True) == 'y' else ' nss-phy-ptp=y'}"

# Pass supported_phys to the build
QCA_NSS_PHY_CONFIG_OPTS:append = " supported_phys='${QCA_NSS_PHY_SUPPORTED_PHYS}'"

do_compile() {
    ${MAKE} -C ${STAGING_KERNEL_BUILDDIR} ${QCA_NSS_PHY_CONFIG_OPTS} M="${S}" modules
}

do_install() {
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}

    #
    # 1) nss_ext/
    #
    if [ -d "${S}/nss_ext" ]; then
        for f in ${S}/nss_ext/*${KERNEL_OBJECT_SUFFIX}; do
            [ -e "$f" ] || continue
            install -m 0644 "$f" ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
        done
    fi

    #
    # 2) linux_std/
    #
    # 2.1) qcom-lnx-phy.ko
    if [ -f ${S}/linux_std/qcom-lnx-phy${KERNEL_OBJECT_SUFFIX} ]; then
        install -m 0644 ${S}/linux_std/qcom-lnx-phy${KERNEL_OBJECT_SUFFIX} \
            ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
    fi

    # 2.2) linux_std subdir
    for sub in clock macsec mdio_ahb ptp qca8084; do
        if [ -d ${S}/linux_std/${sub} ]; then
            for f in ${S}/linux_std/${sub}/*${KERNEL_OBJECT_SUFFIX}; do
                [ -e "$f" ] || continue
                install -m 0644 "$f" ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
            done
        fi
    done

    #
    # 3) headers
    #
    install -d ${D}${includedir}/qca-nss-phy
    for dir in nss_ext linux_std linux_std/clock linux_std/mdio_ahb; do
        if [ -d "${S}/${dir}" ]; then
            for f in ${S}/${dir}/*.h; do
                [ -e "$f" ] || continue
                install -m 0644 "$f" ${D}${includedir}/qca-nss-phy/
            done
        fi
    done
    [ -f ${S}/Module.symvers ] && install -m 0644 ${S}/Module.symvers ${D}${includedir}/qca-nss-phy/Module.symvers

    #
    # 4) Managing the Load Order of KO Modules
    #
    install -d ${D}${sysconfdir}/modprobe.d
    cat <<EOF > ${D}${sysconfdir}/modprobe.d/qca-nss-phy.conf
# Keep QSDK-style load order via native softdep
softdep mdio_ahb pre: mdio_i2c mdio_gpio mdio_bitbang mdio_ipq4019
softdep qca8k_cc pre: mdio_ahb
softdep qca8084_phy pre: qca8k_cc
softdep qcom_lnx_phy pre: qca8084_phy
softdep qca8xxx_phc pre: qcom_lnx_phy
softdep qca_nss_phy pre: qca8xxx_phc
softdep qcom_lnx_phy_macsec pre: qca_nss_phy
EOF
}

RRECOMMENDS:${PN} += "kernel-module-sfp"
# Auto-load minimal; softdep pulls the rest.
KERNEL_MODULE_AUTOLOAD += "qca-nss-phy"
KERNEL_MODULE_AUTOLOAD += "qcom-lnx-phy-macsec"
